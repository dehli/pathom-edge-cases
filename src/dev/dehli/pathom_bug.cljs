(ns dev.dehli.pathom-bug
  (:require [cljs.core.async :refer [go <!]]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(pc/defresolver other-key
  [{id :my/id}]
  {:my/other-key (str "hello, " id)})

(pc/defmutation my-mutation
  [{:keys [parser] :as env} {id :my/id}]
  {::pc/sym 'my/mutation
   ::pc/output [:my/id :my/state]}
  (go
    (let [ident [:my/id id]
          state (atom [])]
      ;; The 1st parse causes the 2nd parse call to return ::p/not-found
      ;; If you add :my/id to 1st query, the 2nd will work.
      (swap! state conj (<! (parser env [{ident [:my/other-key]}])))
      (swap! state conj (<! (parser env [{ident [:my/id]}])))
      {:my/id id
       :my/state (mapv #(get % ident) @state)})))

(def parser
  (p/parallel-parser
    {::p/env     {::p/reader               [p/map-reader
                                            pc/parallel-reader
                                            pc/open-ident-reader
                                            p/env-placeholder-reader]
                  ::p/placeholder-prefixes #{">"}}
     ::p/mutate  pc/mutate-async
     ::p/plugins [(pc/connect-plugin {::pc/register [my-mutation
                                                     other-key]})
                  p/error-handler-plugin
                  p/trace-plugin]}))

(comment
  (go
    (prn (<! (parser {} `[(my/mutation {:my/id "foo"})]))))
  ;;
  ;; #:my{mutation #:my{:state [#:my{:other-key "hello, foo"}
  ;;                            #:my{:id :com.wsscode.pathom.core/not-found}]
  ;;      :id "foo"}}
  ;;
  )

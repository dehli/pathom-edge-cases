(ns dev.dehli.pathom-bug
  (:require [clojure.core.async :refer [go <! <!!]]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.parser :as pp]))

(defn with-entity [env ent]
  (assoc env ::p/entity (atom ent)))

(pc/defresolver one
  [{id :id}]
  {:one (str "one: " id)})

(pc/defresolver state
  [{:keys [parser] :as env} {id :id}]
  {::pc/output [:state]}
  (go
    {:state (<! (parser (with-entity env {:id id}) [:one]))}))

(def parser
  (p/parallel-parser
    {::p/env     {::p/reader               [p/map-reader
                                            pc/parallel-reader
                                            pc/open-ident-reader
                                            p/env-placeholder-reader]
                  ::p/placeholder-prefixes #{">"}
                  ::pp/external-wait-ignore-timeout 100}
     ::p/plugins [(pc/connect-plugin {::pc/register [state
                                                     one]})
                  p/error-handler-plugin
                  p/trace-plugin]}))

(comment
  (<!! (parser {} [{[:id "foo"] [:one :state]}]))

  )

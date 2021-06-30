(ns dev.dehli.pathom-bug
  (:require [clojure.core.async :refer [go <! <!!]]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(defn with-entity [env ent]
  (assoc env ::p/entity (atom ent)))

(pc/defresolver one
  [{id :id}]
  {:one (str "one: " id)})

(pc/defresolver state
  [{:keys [parser] :as env} {id :id}]
  {::pc/output [:state]}
  (go
    (let [state (atom nil)]
      (reset! state (<! (parser (with-entity env {:id id}) [:one])))
      {:state @state})))

(def parser
  (p/parallel-parser
    {::p/env     {::p/reader               [p/map-reader
                                            pc/parallel-reader
                                            pc/open-ident-reader
                                            p/env-placeholder-reader]
                  ::p/placeholder-prefixes #{">"}}
     ::p/plugins [(pc/connect-plugin {::pc/register [state
                                                     one]})
                  p/error-handler-plugin
                  p/trace-plugin]}))

(comment
  (<!! (parser {} [{[:id "foo"] [:one :state]}]))

  )

(ns dev.dehli.pathom3.all-paths-failed
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver resolver-a
  [{in :in}]
  {::pco/output [:out]}
  (when (= in "a")
    {:out "A"}))

(pco/defresolver resolver-b
  [{in :in}]
  {::pco/output [:out]}
  (when (= in "b")
    {:out "B"}))

(def env
  (pci/register [resolver-a resolver-b]))

(comment
  (p.eql/process-one env {:in "a"} (pco/? :out)) ;; => "A"
  (p.eql/process-one env {:in "b"} (pco/? :out)) ;; => "B"
  (p.eql/process-one env {:in "c"} (pco/? :out)) ;; => All paths from an OR node failed. Expected: {:out {}}
  )

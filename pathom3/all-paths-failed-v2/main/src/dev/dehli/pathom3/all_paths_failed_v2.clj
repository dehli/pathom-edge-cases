(ns dev.dehli.pathom3.all-paths-failed-v2
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver one [_]
  {::pco/output [:one]}
  nil)

(pco/defresolver two-a [_]
  {::pco/input [:one]}
  {:two true})

(pco/defresolver two-b [_]
  {::pco/input [:one]}
  {:two true})

(pco/defresolver three [_]
  {::pco/input [:two]}
  {:three true})

(def env
  (pci/register [one two-a two-b three]))

(comment
  (p.eql/process-one env (pco/? :two))
  ;; => nil

  (p.eql/process-one env (pco/? :three))
  ;; => All paths from an OR node failed.
  ;;    Expected: {:two {}}
  )

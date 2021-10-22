(ns dev.dehli.pathom3.extra-keys
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]
            [potpuri.core :as pot]))

(def nodes
  {0 {:node/name "node zero"
      :node/children [{:node/id 1}]
      :node/parent nil}

   1 {:node/name "node one"
      :node/children []
      :node/parent {:node/id 0}}})

(pco/defresolver node
  [{id :node/id}]
  {::pco/output [{:node/children [:node/id]}
                 {:node/parent [:node/id]}
                 :node/name]}
  (get nodes id))

(pco/defresolver index
  [{:node/keys [id parent]}]
  {::pco/input [:node/id
                {:node/parent [(pco/? {:node/children [:node/id]})]}]}
  {:node/index (pot/find-index (:node/children parent)
                               #(= id (:node/id %)))})

(def env
  (pci/register [node index]))

(comment
  (p.eql/process env {:node/id 1} [:node/index :node/parent])
  ;; =>
  ;; {:node/index 0
  ;;  :node/parent {:node/id 0
  ;;                :node/name "node zero"
  ;;                :node/children [{:node/id 1}]
  ;;                :node/parent nil}}

  (p.eql/process env {:node/id 1} [:node/parent])
  ;; =>
  ;; {:node/parent {:node/id 0}}
  )

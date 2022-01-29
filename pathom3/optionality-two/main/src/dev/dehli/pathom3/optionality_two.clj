(ns dev.dehli.pathom3.optionality-two
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver items []
  {::pco/output [{:items [:id {:parent [:id :other.id]}]}]}
  {:items [{:id 0
            :parent {:id 1}}

           {:id 2
            :parent {:other.id 3}}]})

(pco/defresolver parent-type [_]
  {::pco/input [{:parent [(pco/? :id)]}]}
  {:parent-type "foo"})

(def env
  (pci/register [items parent-type]))

(comment
  (p.eql/process env [{:items
                       [:id
                        {:parent [(pco/? :id)
                                  (pco/? :other.id)]}]}])

  ;; Properly returns
  ;; {:items [{:id 0 :parent {:id 1}}
  ;;          {:id 1 :parent {:other.id 1}}]}

  (p.eql/process env [{:items
                       [:id
                        :parent-type]}])

  ;; Pathom can't find a path for the following elements in the query:
  ;; [:id] at path [:items 1 :parent-type]
  )

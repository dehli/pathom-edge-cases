(ns dev.dehli.pathom3.optionality
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver people []
  {::pco/output [{::people [::email]}]}
  {::people [{::email "foo@acme.com"}
             {::email "bar@acme.com"}]})

(pco/defresolver id [{::keys [email]}]
  {::pco/output [::id]}
  (when (= email "foo@acme.com")
    {::id 0}))

(pco/defresolver display-name [{_ ::id}]
  {::display-name "Octocat"})

(def env
  (pci/register [people id display-name]))

(comment
  (p.eql/process env [{::people
                       [::email
                        (pco/? ::display-name)]}])

  ;; Insufficient data calling resolver
  ;; 'dev.dehli.pathom3.optionality/display-name. Missing attrs
  ;; :dev.dehli.pathom3.optionality/id
  ;; {:required #:dev.dehli.pathom3.optionality{:id {}},
  ;;  :available {},
  ;;  :missing #:dev.dehli.pathom3.optionality{:id {}}}
  )

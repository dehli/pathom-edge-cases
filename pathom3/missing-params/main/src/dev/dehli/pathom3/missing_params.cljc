(ns dev.dehli.pathom3.missing-params
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.async.eql :as p.a.eql]
            [promesa.core :as p]))

(defn- text-format? [env]
  (= (:format (pco/params env))
     :text))

(pco/defresolver email-body [env _]
  {:email/body (if (text-format? env)
                 "Hello there"
                 "<h1>Hello there</h1>")})

(pco/defresolver email-subject []
  {:email/subject "Clojure Rocks"})

(pco/defresolver email-valid?
  [{:email/keys [body subject]}]
  {:email/valid? (and (some? body) (some? subject))})

(def env
  (-> (pci/register [email-body email-subject email-valid?])
      (assoc ::p.a.eql/parallel? true)))

(comment
  (letfn [(process [tx]
            (-> env
                (p.a.eql/process tx)
                (p/then prn)))]
    (p/do
      (process [:email/body
                :email/valid?])

      (process `[(:email/body {:format :text})
                 :email/valid?])))

  ;; Clojure
  ;; #:email{:body "<h1>Hello there</h1>", :valid? true}
  ;; #:email{:body "Hello there", :valid? true}

  ;; ClojureScript
  ;; #:email{:body "<h1>Hello there</h1>", :valid? true}
  ;; #:email{:body "<h1>Hello there</h1>", :valid? true}
  )

(ns projects_catalog.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.json]
            [io.pedestal.interceptor.helpers :refer [definterceptor defhandler]]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(def connect-string "mongodb://admin:admin@172.17.0.9:27017/admin")


(defn home-page
 [request]
 (ring-resp/response "Hello world!"))

(defn get-projects
  [request]
  (let [uri connect-string
        {:keys [conn db]} (mg/connect-via-uri uri)]
        (http/json-response
          (mc/find-maps db "projects-catalog"))))

(defn db-get-project [proj-name]
  (let [uri connect-string
        {:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/find-maps db "projects-catalog" {:proj-name proj-name})))

(defn get-project [request]
  (http/json-response
    (db-get-project (get-in request [:path-params :proj-name]))))


(defn add-project
  [request]
      (let [uri connect-string
            incoming (:json-params request)
            {:keys [conn db]} (mg/connect-via-uri uri)]
                  (ring-resp/created
                  "http://my-created-resource-uri"
                  (mc/insert-and-return db "projects-catalog" incoming))))


(defhandler token-check [request]
    (let [token (get-in request [:headers "x-catalog-token"])]
        (if (not (= token "o brave new world"))
            (assoc (ring-resp/response {:body "access denied"}) :status 403)
        )
    )
)


;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
;; (def routes #{["/" :get (conj common-interceptors `home-page)]
;;               ["/about" :get (conj common-interceptors `about-page)]
;;               ["/projects" :get (conj common-interceptors `get-projects)]})

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
(def routes
  `[[["/" {:get home-page}
      ^:interceptors [(body-params/body-params) http/html-body token-check]
      ["/about" {:get about-page}]
      ["/projects" {:get get-projects :post add-project}]
      ["/projects/:proj-name" {:get get-project}]]]])


;; Consumed by projects-catalog.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}
              ;;::http/allowed-headers ["x-catalog-token"]
              ;;::http/allowed-methods ["POST" "GET"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (Integer. (or (System/getenv "PORT") 5000))
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

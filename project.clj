(defproject limitless-dawn-24766 "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.pedestal/pedestal.service "0.5.1"]

                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.1"]
                 ;; [io.pedestal/pedestal.immutant "0.5.1"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.1"]

                 ;; lib to conect clojure with mongoDB
                 [com.novemberain/monger "3.0.2"]
                 ;; light table repl plugin
                 [lein-light-nrepl "0.0.18"]
                 [ch.qos.logback/logback-classic "1.1.7" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.21"]
                 [org.slf4j/jcl-over-slf4j "1.7.21"]
                 [org.slf4j/log4j-over-slf4j "1.7.21"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]

  :repl-options {:nrepl-middlewere [lighttable.nrepl.handler/lighttable-ops]}

  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.3"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "limitless-dawn-24766.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.1"]]}
             :uberjar {:aot [limitless-dawn-24766.server]}}
  :main ^{:skip-aot true} limitless-dawn-24766.server)


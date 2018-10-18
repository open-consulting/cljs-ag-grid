(defproject rum-ag-grid "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0" :exclusions [org.clojure/spec.alpha]]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [hiccups "0.3.0"]
                 [rum "0.11.2" :exclusions [cljsjs/react sablono cljsjs/react-dom]]
                 [org.clojure/core.async "0.4.474" :exclusions [org.clojure/tools.reader]]]
  :plugins [[lein-figwheel "0.5.16"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :profiles {:dev  {:source-paths ["src" "dev"]
                    :dependencies [[devcards "0.2.4"]
                                   [binaryage/devtools "0.9.9"]
                                   [figwheel-sidecar "0.5.16"]
                                   [cider/piggieback "0.3.1"]]
                    :plugins [[lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]
                              [lein-figwheel "0.5.14"]]}}
  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src"]
                :figwheel true
                :compiler     {:main "rum-ag-grid.example"
                               :asset-path "cljs/dev_out"
                               :output-to "resources/public/cljs/site_dev.js"
                               :output-dir "resources/public/cljs/dev_out"}}]}
  :main ^:skip-aot rum-ag-grid.core
  :target-path "target/%s"
  :source-paths ["src"]
  )

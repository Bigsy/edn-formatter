(defproject edn-formatter "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [binaryage/chromex "0.2.0"]
                 [prismatic/dommy "1.1.0"]
                 [binaryage/devtools "0.4.1"]
                 [figwheel "0.5.0-1"]
                 [fipp "0.6.4"]
                 [environ "1.0.1"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]
            [lein-shell "0.4.2"]
            [lein-environ "1.0.1"]
            [lein-cooper "1.1.2"]]

  :figwheel
  {:server-port    7000
   :nrepl-port     7777
   :server-logfile "figwheel_server.log"
   :css-dirs       []}

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["target"
                                    "resources/unpacked/compiled"
                                    "resources/release/compiled"]

  :cljsbuild {:builds {}}                                                                                                     ; prevent https://github.com/emezeske/lein-cljsbuild/issues/413

  :profiles {:unpacked
             {:cljsbuild {:builds
                          {:background
                           {:source-paths ["src/dev"
                                           "src/figwheel"
                                           "src/background"]
                            :compiler     {:output-to             "resources/unpacked/compiled/background/chromex-sample.js"
                                           :output-dir            "resources/unpacked/compiled/background"
                                           :asset-path            "compiled/background"
                                           :optimizations         :none
                                           :anon-fn-naming-policy :unmapped
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :source-map            true
                                           :source-map-timestamp  true}}
                           :edn
                           {:source-paths ["src/dev"
                                           "src/edn"]
                            :compiler     {:output-to             "resources/unpacked/compiled/edn/chromex-sample.js"
                                           :output-dir            "resources/unpacked/compiled/edn"
                                           :asset-path            "compiled/edn"
                                           :optimizations         :whitespace                                                 ; content scripts cannot do eval / load script dynamically
                                           :anon-fn-naming-policy :unmapped
                                           :pretty-print          true
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :source-map            "resources/unpacked/compiled/edn/chromex-sample.js.map"
                                           :source-map-timestamp  true}}
                           :content-script
                           {:source-paths ["src/dev"
                                           "src/content_script"]
                            :compiler     {:output-to             "resources/unpacked/compiled/content_script/chromex-sample.js"
                                           :output-dir            "resources/unpacked/compiled/content_script"
                                           :asset-path            "compiled/content_script"
                                           :optimizations         :whitespace                                                 ; content scripts cannot do eval / load script dynamically
                                           :anon-fn-naming-policy :unmapped
                                           :pretty-print          true
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :source-map            "resources/unpacked/compiled/content_script/chromex-sample.js.map"
                                           :source-map-timestamp  true}}}}}
             :checkouts
             {:cljsbuild {:builds
                          {:background     {:source-paths ["checkouts/chromex/src/lib"
                                                           "checkouts/chromex/src/exts"]}
                           :edn          {:source-paths ["checkouts/chromex/src/lib"
                                                           "checkouts/chromex/src/exts"]}
                           :content-script {:source-paths ["checkouts/chromex/src/lib"
                                                           "checkouts/chromex/src/exts"]}}}}
             :release
             {:env       {:chromex-elide-verbose-logging true}
              :cljsbuild {:builds
                          {:background
                           {:source-paths ["src/background"]
                            :compiler     {:output-to      "resources/release/compiled/background.js"
                                           :output-dir     "resources/release/compiled/background"
                                           :asset-path     "compiled/background"
                                           :optimizations  :advanced
                                           :elide-asserts  true
                                           :compiler-stats true}}
                           :edn
                           {:source-paths ["src/edn"]
                            :compiler     {:output-to      "resources/release/compiled/edn.js"
                                           :output-dir     "resources/release/compiled/edn"
                                           :asset-path     "compiled/edn"
                                           :optimizations  :advanced
                                           :elide-asserts  true
                                           :compiler-stats true}}
                           :content-script
                           {:source-paths ["src/content_script"]
                            :compiler     {:output-to      "resources/release/compiled/content_script.js"
                                           :output-dir     "resources/release/compiled/content_script"
                                           :asset-path     "compiled/content_script"
                                           :optimizations  :advanced
                                           :elide-asserts  true
                                           :compiler-stats true}}}}}}

  :aliases {"dev-build" ["with-profile" "+unpacked" "cljsbuild" "once" "background" "content-script" "edn"]
            "fig"       ["with-profile" "+unpacked" "figwheel" "background"]
            "content"   ["with-profile" "+unpacked" "cljsbuild" "auto" "content-script" "edn"]
            "devel"     ["do" "clean," "cooper"]
            "release"   ["with-profile" "+release" "do" "clean," "cljsbuild" "once" "background" "content-script" "edn"]
            "package"   ["shell" "scripts/package.sh"]})

(ns build
  "clojure -T:build ci

  For more information, run:

  clojure -A:deps -T:build help/doc"
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]
            [clojure.tools.deps :as t]
            [deps-deploy.deps-deploy :as d]))

(def lib 'com.github.reefersleep/thread-until)
(def version "1.0.0")
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (println "\nRunning tests...")
  (let [basis    (b/create-basis {:aliases [:test]})
        combined (t/combine-aliases basis [:test])
        cmds     (b/java-command
                  {:basis basis
                   :java-opts (:jvm-opts combined)
                   :main      'clojure.main
                   :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn- pom-template [version]
  [[:description "Lightweight macros for Railway-oriented programming"]
   [:url "https://github.com/Reefersleep/thread-until"]
   [:licenses
    [:license
     [:name "The MIT License"]
     [:url "http://opensource.org/licenses/MIT"]]]
   [:developers
    [:developer
     [:name "Reefersleep"]]]
   [:scm
    [:url "https://github.com/Reefersleep/thread-until"]
    [:connection "scm:git:git@github.com:Reefersleep/thread-until.git"]
    [:developerConnection "scm:git:git@github.com:Reefersleep/thread-until.git"]
    [:tag (str "v" version)]]])

(defn- jar-opts [opts]
  (assoc opts
         :lib lib   :version version
         :jar-file  (format "target/%s-%s.jar" lib version)
         :basis     (b/create-basis {})
         :class-dir class-dir
         :target    "target"
         :src-dirs  ["src"]
         :pom-data  (pom-template version)))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (test opts)
  (b/delete {:path "target"})
  (let [opts (jar-opts opts)]
    (println "\nWriting pom.xml...")
    (b/write-pom opts)
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println "\nBuilding" (:jar-file opts) "...")
    (b/jar opts))
  opts)

(defn deploy "Deploy the JAR to Clojars." [opts]
  (let [{:keys [jar-file] :as opts} (jar-opts opts)]
    (d/deploy {:installer :remote :artifact (b/resolve-path jar-file)
               :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))}))
  opts)

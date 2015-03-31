(ns word-counter-jp
  (:require [clojure.string :refer [lower-case split]])
  (:import [org.atilika.kuromoji Token Tokenizer]))

;; 単語の出現回数
(defrecord WordFreq [word category count]
  Object
  (toString [this]
    (str "(" (:word this) " " (:category this) " " (:count this) ")"))

  Comparable
  ;; カウントの降順、単語の昇順、品詞の昇順
  (compareTo [this other]
    (cond
      (not= (:count this) (:count other))
        (compare (:count other) (:count this))
      (not= (:word this) (:word other))
        (compare (:word this) (:word other))
      :else
        (compare (:category this) (:category other)))))

(defn- to-wordfreq [t]
  (let [category (first (split (.getPartOfSpeech t) #","))
        word (fn [t]
               (if (#{"動詞", "形容詞", "助動詞"} category)
                 (.getBaseForm t)
                 (.getSurfaceForm t)))]
    (->WordFreq (word t) category 1)))

(defn- word-elem? [wf]
  (and (some? (:word wf))
       (not (#{"記号"} (:category wf)))))

(defn- wfs-to-wf [[wf wfs]]
  (->WordFreq (:word wf) (:category wf) (count wfs)))

;; 文字列を単語の出現回数のリストに編集する。
(defn- wordfreq-list [s]
  (let [^Tokenizer tokenizer (.build (Tokenizer/builder))]
    (->> s (.tokenize tokenizer) (map to-wordfreq) (filter word-elem?))))

(defn count-words
  "和文の文字列に含まれる単語の出現回数をカウントする。
  活用形は原形に戻し、記号類は除外する。"
  [s]
  (->> s wordfreq-list (group-by identity) (map wfs-to-wf) sort))

(if (not= (count *command-line-args*) 2)
  (println "Please specify an input file.")
  (let [[_ f] *command-line-args*]
    (spit "result.txt" (count-words (slurp f)))))

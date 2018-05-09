(ns data-mine.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.data.csv :as csv])
  (:gen-class))

(defn make-boxes
  "returns an array of fuctions whos associated used to
  determine where amoung the stars a bar goes."
  [high oc1 oc2 low]
  [#(> % high)
   #(= % high)
   #(and (< % high) (> % oc1))
   #(= oc1 %)
   #(and (< % oc1) (> % oc2))
   #(= % oc2)
   #(and (< % oc2) (> % low))
   #(= % low)
   #(< % low)])

(def stars-and-bars "map of all the possible combinations of strings with 8 bars and 4 stars to a number"
  (let [stars-and-bars-keys (map #(keyword (apply str %))
                                 (filter #(= (frequencies %) {"*" 4  "|" 8})
                                         (combo/selections ["*" "|"] 12)))]
    (zipmap stars-and-bars-keys (range))))


(s/def ::stars-bars-map (s/map-of #(let [frq (frequencies (seq (name %)))]
                              (and (= (get frq \*) 4) (= (get frq \|) 8)))  number? :count 495 :conform-keys true))
(s/explain ::stars-bars-map  stars-and-bars)


(s/def ::find-box (s/coll-of fn? :count 9))

(s/def ::candel-point number?)
(s/def ::candel-point-index integer?)

(s/def ::candel-points (s/coll-of ::candel-point :count 4))
(s/def ::candel-point-indexes (s/coll-of ::candel-point-index :count 4))

(defn find-box
  "finds amoung the positions which position 'box-num' between the bars this star goes.
  boxes is an array of functions who's truth value determinse presence or absence of a star.
  candle-point is an open high low close value of a stock candel"
  [boxes candel-point]
  {:pre [(s/valid? ::find-box boxes) (s/valid? ::candel-point candel-point)]
   :post [(s/valid? ::candel-point-index %)]}
  (some (fn [[at? position]] (if (at? candel-point) position)) (zipmap boxes (range))))

(defn get-boxes
  "maps [open high low close] values of candel2 to these values position in a stars and bars representation"
  [boxes candel-points]
  {:pre [(s/valid? ::candel-points candel-points)]
   :post [(s/valid? ::candel-point-indexes %)]}
  (mapv #(find-box boxes %) candel-points))

(s/def ::bar-star-part #(re-matches #"\**\|?" %))
(s/def ::bars-and-stars-result #(re-matches #"[*|]*" %))

(defn output-box-string [[box-num num-stars]]
  {:post [(s/valid? ::bar-star-part %)]}
  (str (apply str (repeat num-stars "*")) (if (< box-num 8) "|" "")))

(defn get-candel-key
  "uses the array of functions 'boxes' who's truth value during an iteration of the loop 'i' determines whether"
  [boxes candel2]
  {:post [(s/valid? ::bars-and-stars-result %)]}  
  (let [frq (frequencies (get-boxes boxes candel2))
        frq (merge (zipmap (range) (repeat (count boxes) 0)) frq)
        frq (into (sorted-map-by <) frq)]
    (apply str (map output-box-string frq))))

(defn candelkey
  "returns the stars and bars representation of the candel"
  [c1 c2]
  (keyword (get-candel-key (apply make-boxes c1) c2)))

(defn red-green [c1 c2]
  (let [[o1 _ _ c1] c1
        [o2 _ _ c2] c2
        label1   (if (> c1 o1) :green :red)
        label2   (if (> c2 o2) :green :red)]
    (case [label1 label2]
      [:green :green] 0
      [:red :green] 1
      [:green :red] 2
      [:red :red] 3)))
    


(defn reverse-comarator [a b] (compare b a))


(def candel-key (candelkey
                 (sort reverse-comarator [164.12 165.73  163.37  164.22])  (sort reverse-comarator [164.00  164.33 160.63 162.32])))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "AAPL stock")
  (println "Date  Open High Low  Close* Adj Close**  Volume")
  (println "Apr 27, 2018  164.00 164.33 160.63 162.32") 
  (println "Apr 26, 2018  164.12 165.73 163.37 164.22")
  (println "This program assigns a number to each way the prices of 2 adjacent stocks can interleave.  In this way, this program labels stock data.")
  (println "There are 495 ways a interleave the open high low close of 2 green stock candels and (* 4 495) ways to interleave any 2 colored candels.
An interleaving may include situations where one of the adjacent days match.
This means there are 9 ways for the close of any second day to be between or on a price of the first day.
 Let each way be called a box.  Finding the number of ways the 4 prices of the second day can be put in the boxes of the first day is a combinatorics problem.
https://en.wikipedia.org/wiki/Stars_and_bars_(combinatorics)
This programs has a hash with all of the combinations of 8 bars and 4 stars as keys, and the values of the hash are 0 through 494 or '(range 495)'.
The hash is a lookup table for all the combinations of stars and bars.
Accounting for the differences stock candel colors is done with assigning a number to each of the 4 ways 2 colors can appear on two adjacent bars.
This program labels as an example the 27th of April 2018 for Apple stock.  It can, however, be applied to any 2 adjacent days of any stock.")
  (println (str "the candel in stars and bars form is:" candel-key))
  (println "its label is:")
  (println (+ (get stars-and-bars candel-key) (* 495 (red-green [164.12 165.73 163.37 164.22] [164.00 164.33 160.63 162.32])))))

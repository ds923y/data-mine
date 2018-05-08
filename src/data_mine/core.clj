(ns data-mine.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


  (def stars-and-bars "map of all the possible combinations of strings with 8 bars and 4 stars to a number"
    (let [stars-and-bars-keys (map #(keyword (apply str %))
                                   (filter #(= (frequencies %) {"*" 4  "|" 8})
                                           (combo/selections ["*" "|"] 12)))] 
      (zipmap stars-and-bars-keys (range))))


(defn find-box
  "finds amoung the positions which position 'box-num' between the bars this star goes.
  boxes is an array of functions who's truth value determinse presence or absence of a star.
  candle-point is an open high low close value of a stock candel"
  [boxes candel-point]
  (loop [box-cond boxes box-num 0]
    (if ((first box-cond) candel-point)
      box-num
      (recur (rest box-cond) (inc box-num)))))


;(map-indexed (fn [idx itm] ()) boxes)

(defn get-boxes
  "maps [open high low close] values of candel2 to these values position in a stars and bars representation"
  [boxes candel2]
  (mapv #(find-box boxes %) candel2))


(defn output-box-string [[box-num num-stars]]
  (str (apply str (repeat num-stars "*"))
       (if (< box-num 8) "|" "")))

(defn get-candel-key
  "uses the array of functions 'boxes' who's truth value during an iteration of the loop 'i' determines whether"
  [boxes candel2]
  (let [frq (frequencies (get-boxes boxes candel2))
        frq (merge (zipmap (range) (repeat (count boxes) 0)) frq)
        frq (into (sorted-map-by <) frq)]
    (apply str (map output-box-string frq))))


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

(defn candelkey
  "returns the stars and bars representation of the candel"
  [c1 c2]
    (get-candel-key (apply make-boxes c1) c2))


(defn red-black [c1 c2]
  (let [[o1 _ _ c1] c1
        [o2 _ _ c2] c2
        label1   (if (> c1 o1) :green :red)
        label2   (if (> c2 o2) :green :red)]
    (case [label1 label2]
      [:green :green] 0
      [:red :green] 1
      [:green :red] 2
      [:red :red] 3)))

(defn high-med-low-tiny [c1 c2]
  (let [[o1 _ _ c1] c1
        [o2 _ _ c2] c2
        label1   (if (> c1 o1) :green :red)
        label2   (if (> c2 o2) :green :red)]
    (case [label1 label2]
      [:green :green] 0
      [:red :green] 1
      [:green :red] 2
      [:red :red] 3)))
    
  ;  Date	Open	High	Low	Close*	Adj Close**	Volume
  ;  Apr 27, 2018	164.00	164.33	160.63	162.32	
                                        ;  Apr 26, 2018	164.12	165.73	163.37	164.22
(defprotocol P 
  (bin-bar [this itm]))

(deftype BinBars [wd #^{:unsynchronized-mutable true} q] P
  (bin-bar [this itm]
    (set! q (conj q itm))
    (if (= (count q) wd)
      (let [fst (peek q)
            mn  (apply min q)
            mx  (apply max q)
            tpartn (reverse (sort q))
            b0 (nth tpartn (int (* 0.05 (count q))))
            b1  (nth tpartn (int (* 0.10 (count q))))
            b2  (nth tpartn (int (* 0.25 (count q))))
            b3  (nth tpartn (int (* 0.75 (count q))))
            b4  (nth tpartn (int (* 0.90 (count q))))]
        (set! q (pop q))
        (cond ;(< itm b4) 0
                (and #_(>= itm b4) (< itm b3)) 1
                (and (>= itm b3) (< itm b2)) 2
                (and (>= itm b2) (< itm b1)) 3
                (and (>= itm b1) (< itm b0)) 4
                (>= itm b0) 5)))))

(def binner (->BinBars 100 (clojure.lang.PersistentQueue/EMPTY)))

(defn- ticker-data-io []
  (with-open [reader (io/reader "/Users/drewshaw/Documents/ConduiteDataProcessors/eod-data/AAPL.csv")]
    (mapv #(Math/abs (- (Double/parseDouble (get % 1)) (Double/parseDouble (get % 4)))) (csv/read-csv reader))) #_(mapv row-cast ))

(def dtaa (ticker-data-io))

(println (mapv #(.bin-bar binner %) (ticker-data-io)))

;(.bin-bar binner 33)

(def ky (keyword (candelkey
         (reverse (sort [164.12    165.73	163.37	164.22]))  (reverse (sort [164.00  164.33 160.63 162.32])))))


(+ (get stars-and-bars ky) (* 495 (red-black [164.12 165.73 163.37 164.22] [164.00 164.33 160.63 162.32])))

(* (* 495 4) 5)

(println ky)
(reverse (sort [7 1 2 3 4 5]))

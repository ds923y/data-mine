(ns data-mine.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(def df (zipmap (map #(keyword (apply str %)) (filter #(= (frequencies %) {"*" 4  "|" 8}) (combo/selections ["*" "|"] 12))) (range)))


(defn find-box [boxes candel-point]
  (loop [box-cond boxes box-num 0]
    (if ((first box-cond) candel-point)
      box-num
      (recur (rest box-cond) (inc box-num)))))


;(map-indexed (fn [idx itm] ()) boxes)

(defn get-boxes [boxes candel2]
  (loop [candel-points candel2 box-nums []]
    (let [pt (seq candel-points)]
      (if-not pt
        box-nums
        (recur (rest pt) (conj box-nums (find-box boxes (first pt))))))))


;(merge (zipmap (range) (repeat 9 0)) (frequencies (get-boxes boxes candel2)))


(defn nxt-str [frq i]
  (let [fmr (get frq i)]
   (if-not fmr
    (if (< i 8) "|" "")
    (str (apply str (repeat fmr "*")) (if (< i 8) "|" "")))))

(defn get-candel-key [boxes candel2]
  (let [frq (frequencies (get-boxes boxes candel2))] 
    (loop [i 0 h-code ""]
      (let [tocat (str h-code (nxt-str frq i))]
        (if (= i 8)
          tocat
          (recur (inc i) tocat))))))


  (defn make-boxes [high oc1 oc2 low]
    [#(> % high)
     #(= % high)
     #(and (< % high) (> % oc1))
     #(= oc1 %)
     #(and (< % oc1) (> % oc2))
     #(= % oc2)
     #(and (< % oc2) (> % low))
     #(= % low)
     #(< % low)])

  (defn candelkey [c1 c2]
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


(+ (get df ky) (* 495 (red-black [164.12 165.73 163.37 164.22] [164.00 164.33 160.63 162.32])))

(* (* 495 4) 5)



(reverse (sort [7 1 2 3 4 5]))

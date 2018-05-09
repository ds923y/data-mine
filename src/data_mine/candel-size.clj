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



                                        ;(defn- ticker-data-io []
                                        ;  (with-open [reader (io/reader "/Users/drewshaw/Documents/ConduiteDataProcessors/eod-data/AAPL.csv")]
                                        ;    (mapv #(Math/abs (- (Double/parseDouble (get % 1)) (Double/parseDouble (get % 4)))) (csv/read-csv reader))) #_(mapv row-cast))

                                        ;(def dtaa (ticker-data-io))
                                        ;
                                        ;(println (mapv #(.bin-bar binner %) (ticker-data-io)))

                                        ;(.bin-bar binner 33)

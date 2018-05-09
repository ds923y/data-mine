# data-mine

"This program assigns a number to each way the prices of 2 adjacent stocks can interleave.  In this way, this program labels stock data."
There are 495 ways a interleave the open high low close of 2 green stock candels and (* 4 495) ways to interleave any 2 colored candels.
An interleaving may include situations where one of the adjacent days match.
This means there are 9 ways for the close of any second day to be between or on a price of the first day.
 Let each way be called a box.  Finding the number of ways the 4 prices of the second day can be put in the boxes of the first day is a combinatorics problem.
https://en.wikipedia.org/wiki/Stars_and_bars_(combinatorics)
This programs has a hash with all of the combinations of 8 bars and 4 stars as keys, and the values of the hash are 0 through 494 or '(range 495)'.
The hash is a lookup table for all the combinations of stars and bars.
Accounting for the differences stock candel colors is done with assigning a number to each of the 4 ways 2 colors can appear on two adjacent bars.
This program labels as an example the 27th of April 2018 for Apple stock.  It can, however, be applied to any 2 adjacent days of any stock."
## Installation


## Usage

$ java -jar data-mine-0.1.0-standalone.jar 

## Options

FIXME: listing of options this app accepts.

## Examples

### Bugs


## License

Copyright © 2018 Drew Shaw

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
# data-mine

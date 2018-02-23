# MonteCarlo_Poker
This program runs thousands of hand simulations to calculate the likelihood of a hand winning in texas hold 'em.
Any configuration of cards can be specified, so if you want to know what the odds of winning are if you have a 9-Jack off suit, and the flop has two 2's and a Jack, you can do that. Additionally, the program accounts for potential folding of opponents. If the opponent has a bad hand, s/he will fold early, possibly missing his/her chance to pick up a winning card on the turn or river.

The table statistics are printed out to display the probabilities of various occurences: the expected number of pairs, straights, triples, and other combinations; the expected number of times of having the best hand.

Two separate decks are displayed each time, so the user can quickly compare the probability difference if the hand has a Jack of Hearts instead of a Jack of Diamonds.

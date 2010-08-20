
                One dimensional bin packing

In 1d-binpacking we have a bag of numbers and a collection of 
"bins", where each bin has the same capacity. The problem is to "pack"
the numbers into the bins such that all of the numbers are packed,
the sum of the numbers in each bin is less than or equal to the 
capacity of the bin, and as few bins as possible are used.

You are to implement 2 methods in the BinPack class: pack and isLegalPacking.

pack()
------
This method takes the data in data[] and packs them into the 
bins, i.e. bin[]. You should use methods add() and remove() 
in class Bin.

isLegalPacking()
----------------
This method delivers true if the packing is legal, false otherwise.
In a legal packing all items in data are packed and all bins are legal.


1. Copy ALL files in this directory into a NEW directory
2. Edit the file BinPack.java implementing the two methods above
   You can also edit Bin.java if you wish to.
3. Use the Test program to test out your methods. This was described 
   in the lecture. If you were not present at the lecture, ask a student
   who was present at the lecture.
4. To Submit:
     > cp BinPack.java ~/cs233lab/BinPack.java
     > cp Bin.java ~/cs233lab/Bin.java
     > cs233check

Deadline, Friday 24th March 2000, at 5 o'clock

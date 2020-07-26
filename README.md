TO BE NOTICED
=============
=> As the inputs are non structured CSV files, I tried my level best to achieve this.

CORRECTIONS MADE ACCORDING TO MY ASSUMPTIONS
============================================
=> As every transaction description is being ended with location name
   |--> In ICICI-Input-Case2.csv
   |__> I changed 12-03-2018,Amazon Cash Back Jan 18            ,213,  to 12-03-2018,Amazon Cash Back Jan Banglore            ,213,
   |__> I removed the extra "," in b/w PAYMENT and MUMBAI 
   |__> 19-03-2018,AIRTEL PAYMENT,MUMBAI              ,, 1297 to 19-03-2018,AIRTEL PAYMENT MUMBAI              ,, 1297
   |__> I changed Transaction word to Transactions as it was plural in every other file just to maintain integrity
   |__> ,,International Transaction,, to ,,International Transactions,,

=> As some banks only have Amount but not credit and debit seperately I considered amount as debit and left credit as empty for those. 

SETUP
=====
=> Make sure that java is installed
=> Inside "mAssignment/" folder open your terminal/command prompt
=> Type the below commands

COMMANDS
========
$ javac Main.java
$ java Main         ==> To get all the transactions in standardized format of that particular bank
$ java Main [name]  ==> [name] passed as the command line argument is to show only transactions belonging to that particular card name
  |
  |__> ex: java Main Rahul ===> This will generate only transactions related to Rahul
  
OUTPUT
======
=> Now a folder named "outputs/" is created inside the current directory i.e., "mAssignment/"
=> Change the current directory to "mAssignment/outputs/"
=> Now you can see the outputs of the input files present in "mAssignment/inputs/" directory
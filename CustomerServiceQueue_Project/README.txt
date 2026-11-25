CUSTOMER SERVICE QUEUE - PS NO. 35
====================================

Requirements:
- JDK 17 or later
- Command Prompt / PowerShell / Terminal

Files:
- CustomerServiceQueue.java : Complete source code (all classes are in one Java file)
- customers.txt              : Data file; the program creates/updates it on exit

How to run on Windows:
1. Extract this ZIP file.
2. Open PowerShell or Command Prompt in the extracted folder.
3. Compile:
   javac CustomerServiceQueue.java
4. Run:
   java CustomerServiceQueue

Menu:
1. Join Queue
2. Call Next Customer
3. Display Queue
4. Leave Queue (by Token)
5. Exit

Notes:
- First generated token is Q101.
- Customers are served in FIFO order.
- The program uses a custom singly linked list (Node, front and rear).
- The data file stores a #NEXT_TOKEN line so token numbers remain unique
  even after customers are served or leave the queue.
- Customer records are saved when option 5 (Exit) is selected.
- Keep customers.txt in the same folder as the Java program.

Example customers.txt format:
#NEXT_TOKEN=103
Q101|Pooja|Account Opening
Q102|Vikram|Loan Enquiry

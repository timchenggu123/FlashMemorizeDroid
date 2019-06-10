# FlashMemorizeDroid

Developed using Android Studio

This is the android version of the desktop app FlashMemorize!

The Desktop version can be found [here] (https://github.com/timchenggu123/FlashMemorize) (the desktop version has not been updated in a while, so it lacks some features supported by the Android version. A major update is going to come up soon hopefully).

##About

FlashMemorize! is an app that can turn a text file into a deck of flashcards. 

##Creating a deck

FlashMemorize reads a text file and generate a deck from it. The syntax for a text deck is really simple. The following tutorial will create a deck using Notepad on Windows, but you can also create decks using any text editor inluding vim, nano, pico, VS Code, etc. Just make sure your file has a .txt extention.

1. Create a .txt file, name it however you like, then open it. 
2. Write down what you want to have on the front side of your first flashcard. 
3. If you want to insert a new line on the front side, instead of hitting *enter*, type **" -"** (without the quotation marks, and **with a space** in front of the dash). This is the equivalent of a new line character. Note, if you want to type out a bullet list, you can simply type **" --"** which will display *one* dash at the start of a new line
4. If you want to insert a picture, you need to first put the image file in the same folder as the .txt file, then in the .txt file, type out **"{MyImage.PNG}"** (replace MyImage.PNG with the file name of your image).
5. After you are done editing the front side of your first card, hit the **Tab** key (also known as the **\t** character). **Note: You are still on the same line!**
6. Enter what you want to have on the back side of the card following step 3 & 4.
7. After you are done editing the back side of your card, hit **Enter** key (also known as the **\n** character). Now you should be one a new line in your .txt file. This will create a new card.
8. Repeat step 2-7 to complete your deck!

##Opening a deck

The are a couple ways you can get your deck file onto your phone.

The simplest way is to connect your phone via usb, then drag the file into a folder of your choice on your phone. (If you have pictures inlcuded in the deck, make sure to also put them *under the same directory* as the .txt file)

Alternatively, you could also upload the file into google drive. **Note: The Google Drive works with the default android file manager. If your phone uses a custome file manager, this might not work. Moreover, Google Drive methdod does not support pictures.**

Onec you have the file one your phone or on google drive, you can launch FlashMemorize on your phone. On the main screen, tap the cicular "+" floating button. This should open the file manager. Navigate to your file and tap it. 

It might take a while for the file to load, please wait with patience. After loading is complete, you should see the deck on the main screen.

Tap the deck to open!



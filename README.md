# FlashMemorizeDroid

Coming to Google Play soon!

Download the APK [here](https://drive.google.com/file/d/14sAtLcSB7w27o3-7ZiY6Qe4wkSCokMPi/view?usp=sharing)

Developed using Android Studio

This is the android version of the Windows desktop app FlashMemorize!

The Windows version can be found [here](https://github.com/timchenggu123/FlashMemorize) (the desktop version has not been updated in a while, so it lacks some features supported by the Android version. A major update is going to come up soon hopefully).

## About

FlashMemorize! is an app that can turn a text file into a deck of flashcards. 

## Creating a deck

FlashMemorize reads a text file and generate a deck from it. The syntax for a text deck is really simple. The following tutorial will create a deck using Notepad on Windows, but you can also create decks using any text editor inluding vim, nano, pico, VS Code, etc. Just make sure your file has a .txt extention.

1. On your computer, create a .txt file, name it however you like, then open it. 
2. Write down what you want to have on the front side of your first flashcard. 
3. If you want to insert a new line on the front side, instead of hitting *enter*, type **" -"** (without the quotation marks, and **with a space** in front of the dash). This is the equivalent of a new line character. Note, if you want to type out a bullet list, you can simply type **" --"** which will display *one* dash at the start of a new line
4. If you want to insert a picture, you need to first put the image file in the same folder as the .txt file, then in the .txt file, type out **"{MyImage.PNG}"** (replace MyImage.PNG with the file name of your image). After you are done creating the deck, compress the folder with the .txt file into a **.zip** file. Make sure the **.zip** file has **the same file name** as the **.txt** file. E.g. a zip file containing "myDeck.txt" would be named "myDeck.zip".
5. After you are done editing the front side of your first card, hit the **Tab** key (also known as the **\t** character). **Note: You are still on the same line!**
6. Enter what you want to have on the back side of the card following step 3 & 4.
7. After you are done editing the back side of your card, hit **Enter** key (also known as the **\n** character). Congraduations, you have just finished creating your first card! Now you should be on a new line in your .txt file. This will create a new card.
8. Repeat step 2-7 to complete your deck!

## Opening a deck

The are a couple ways you can get your deck file onto your phone.

The simplest way is to connect your phone via usb, then drag the file into a folder of your choice on your phone. (If you have pictures included in the deck, make sure they are all compressed into a zip file. **Refer to Step 4 of creating decks**)

Alternatively, you could also upload the file on Google Drive. **Note: The Google Drive method works with the default android file manager on newer versions of the Android system. If your phone uses a custom file manager, you might need to download the file in your browser first then open it from your file manager in app (the downloaded files are usually located in the Download/Downloads folder). The general idea of this method can be applied to almost any cloud storage services**

Once you have the file on your phone or on google drive, you can launch FlashMemorize on your phone. On the main screen, tap the cicular "+" floating button. This should open the file manager. Navigate to your file and tap it. 

It might take a while for the file to load, please wait with patience. After loading is complete, you should be able to see the deck on the main screen.

Tap the deck to open it. Now you can begin studying!

## Exporting your deck

Once you have a deck opened using FlashMemorizeDroid you can export it as a .json file. The .json file contains all the text and pictures as well as your user data and can be used across devices. To export the file, navigate to the main screen of the app. Tap the menu icon on the top-right corner. In the drop-down menu, tap "Export Deck". Then, tap the deck you want to export on the main screen. An app selector should show up. Select a method you would like to use to share the .json file!

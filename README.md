# DigiDevil

Code and PDF samples for our presentation in BSides Vilnius 2024

Library used: iText 7.2.6 : https://github.com/itext/itext-java/releases
For pdf layers we used html2pdf library and html files (example html files in source folder)

1.	Content display changes over time - because PDF supports JavaScript, it is possible to generate a file whose content display changes over time.
Open the JS_Animated_Unsuported_JS_2_Layer_Animated.pdf document using Foxit or Adobe Acrobat Reader program and observe how the document content display changes every second.
ChangeOnFileName.java - Code for example.
2.	Layer visibility in printing actions - since PDF supports JavaScript, it is possible to generate a file whose content changes when printing it.
Open the Layer_Visibility_On_Print.pdf document using Foxit or Adobe Acrobat Reader program and try to print it. When you start printing the document, its content display changes.
OnPrint.java - Code for example.
3.	Content change in case of document renaming - the same problem applies - using JavaScript code, the content display changes when the document name is changed, while the signature remains valid. 
OnNameChange.pdf - View the file content using Foxit or Adobe Acrobat Reader program and pay attention to the "TOTAL DUE" field. Then change the document name to good.pdf and after opening the document with Foxit or Adobe Acrobat Reader, you will see that the content display has changed. 
LayerAnimatedHtml_unsuported.java - Code for example.




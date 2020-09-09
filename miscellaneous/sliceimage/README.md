#SliceImage
## Beta version


This sample shows how to print big images using ImageHelper Class

In some cases, we need to print extensive receipts images end the printer run in problem or print unreadable characters.
  
To solve this, you can use ImageHelper class to print small pieces of one big image.

You should assume that you have one image with width  <= your printer width dots capacity.
In case of this sample, our generic printer have capacity to print images with width max 576 dots.
but to get better results you need to read your printer's manual to discover this parameter.

In this case, we need to have one image with 576 pixels max width.

How to use:
```java
        BufferedImage  image = SliceImage.getImage(sampleImages.big_image_576);

        ImageHelper helper = new ImageHelper();

        Bitonal algorithm = new BitonalThreshold();
        GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper(); 


        helper.write(escPos, new CoffeeImageImpl(image),imageWrapper,algorithm);
```

Supose that your printer have spec for 420 dots, then you need to inform this:  
```java
        ImageHelper helper = new ImageHelper(420, 48);
```



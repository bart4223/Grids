# Grids

Grids is a simple graphic tool to draw pixel graphics...


# Configuration

The configuration file must place under resources/config.gcf (grid config file).
   -    GridCount
        -   numbers of initialized grid stages (1..10)
   -    GridMaxDistance
        -   maximum grid raster distance
   -    GridMaxWidth
        -   maximum grid width
   -    GridMaxHeight
        -   maximum grid height
   -    ImageFileSizeX
        -   X-Size for Save as PNG
   -    ImageFileSizeY
        -   Y-Size for Save as PNG
   -    MegaGridPixelSize
        -   Size of MegaPixel
   -    ColorQuantizeFactor
        -   Colors to quantize the image          

# Save/Load

-   To store and load raw data use "save as gdf" (grid data file) function.
-   To store geometry object data use "save as gof" (grid object file) function.
-   To store as png file use "save as png" function.
-   To store as png file with grids use "save as png with grids" function.
-   To load from png file use "load from png" function.
-   To load from png file with color quantization use "load from png with CQ" function.

# Script

-   Grid.ShowGrid
    -   Show the grid
-   Grid.HideGrid
    -   Hide the grid
-   Grid.SetGridDistance
    -   Set the grid distance    
-   Grid.LoadImageWithCQ
    -   Load a image from file with the color quantization dialog
-   Grid.ShowMegaPixel
    -   Show the mega pixel grid     
-   GridHideMegaPixel
    -   Hide the mega pixel grid     

# History

    25.05.2015  Select all and move all selected points via ALT
    26.07.2017  Octree color quantization support
    30.07.2017  Color quantization dialog
    25.08.2017  Application script support implemented  


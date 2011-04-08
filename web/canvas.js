var view=document.getElementsByTagName("canvas")[0];
var viewWidth=view.clientWidth;
var viewHeight=view.clientHeight;
var viewx=0;
var viewy=0;

var context=view.getContext("2d");

var cache = new Array(); //2d array of imageDatas
var chunks = new Array(); //2d array of chunk imageDatas
var emptyChunks = new Array(); //[x,z] of chunks that do not exist on the server
var emptyPixels = new Array(); //real coords of completely transparent pixels
var obsolete = new Array(); //real coords of pixels that need to be rendered

var queue = new Array();
 
var queueSpace = 3;

var get = function(x, y) {
    var result = [0, 0, 0, 0]
    if(emptyPixels.indexOf([x, y]) == -1){
        result = cache[x]
        if(result != undefined) {
            result = cache[x][y];
        }
        if(result == undefined) {
            result = render(x, y);
        }
    }
    return result;
}

var render = function(pixelx, pixely){
    if(obsolete.indexOf(pixelx, pixely) != -1){
        delete obsolete[obsolete.indexOf(pixelx, pixely)];
    }
    //ENGAGE SUPER CHUNK LOCATION CALCULATION
    var gridx = Math.floor((pixelx+viewx)/96);
    var gridy = Math.floor((pixely+viewy)/48);
    var cx = gridx;
    var cz;
    cz = (cx-gridy)/2;
    cx-=cz;
    var topChunk= [Math.floor(cx), Math.floor(cz)];
    var chunkStack = new Array();
    var currentChunk = topChunk;
    if( (gridx+gridy) % 2 == 0 ) {
        var direction = 0; // Left
    } else {
        var direction = 1; // Right
    }
    chunkStack.push(topChunk);
    for(var i = 1; i <= 18; i++) {
        if(direction == 0){
            currentChunk[1]--;
            direction = 1;
        }
        else{
            currentChunk[0]++;
            direction = 0;
        }
        chunkStack.push(currentChunk);
    }
    var pixel = [0, 0, 0, 0];
    console.log(chunkStack);
    while(chunkStack.length > 0){
        chunk = chunkStack.pop();
        var chunkx = chunk[0]; var chunkz = chunk[1];
        if((chunks[chunkx] == undefined || chunks[chunkx][chunkz] == undefined) && emptyChunks.indexOf([chunkx, chunkz]) == -1) {
            enqueue(chunkx, chunkz);
        } else if(emptyChunks.indexOf([chunkx, chunkz]) == -1) {
            var chunkorigx = 6*16*(chunkx+chunkz);
            var chunkorigz = 3*16*(chunkz-chunkx);
            var chunkPixel = chunks[chunkx][chunkz].getImageData(x-chunkorigx, y-chunkorigy, 1, 1);
            pixel = composite(pixel, chunkPixel)
        }
    }
    if(pixel[3] != 0){
        cache[pixelx][pixely] = pixel;
        if(emptyPixels.indexOf(pixelx, pixely) =! -1){
            delete emptyPixels[emptyPixels.indexOf(pixelx, pixely)];
        }
    } else if(emptyPixels.indexOf([pixelx, pixely]) == -1) {
        emptyPixels.push([pixelx, pixely]);
    }
    return pixel;
}

var composite = function(bgcolor, fgcolor){
    var r = (fgcolor[0] - bgcolor[0])*fgcolor[3]/255 + bgcolor[0]
    var g = (fgcolor[1] - bgcolor[1])*fgcolor[3]/255 + bgcolor[1]
    var b = (fgcolor[2] - bgcolor[2])*fgcolor[3]/255 + bgcolor[2]
    var a = bgcolor[3] + fgcolor[3]
    if( a > 255 ) { a = 255; }
    return [r, g, b, a]
}

var enqueue = function(x, z){
    if(queue.indexOf([x, z]) == -1){
        queue.push([x, z]);
    }
    if(queueSpace > 0 && queue.length > 0){
        dl(queue.pop());
    }
}

var obsoleteChunk = function(X, Z){
    chunkx=6*16*(X+Z);
    chunkz=3*16*(Z-X);
    for(var x=0; x<191; x++){
        for(var y=0; y<864; y++){
            if(chunks[X][Z][x][y][3] != 0 && obsolete.indexOf([chunkx+x, chunkz+z]) == -1){
                obsolete.push([chunkx+x, chunkz+z]);
            }
        }
    }
}

var dl = function(chunk){
    queueSpace--;
    var x = chunk[0]; var z = chunk[1];
    var image = new Image();
    image.onload = function() {
        var chunkcanvas = document.createElement("canvas");
        chunkcanvas.width = 191;
        chunkcanvas.height = 864;
        var chunkcontext = chunkcanvas.getContext("2d");
        chunkcontext.drawImage(image, 0, 0);
        var imageData = chunkcontext.getImageData(0, 0, 191, 864);
        var pixelArray = new Array();
        for(var i=0; i<imageData/4; i++) {
            pixelArray[i%191][Math.floor(i/191)]=[imageData[i*4], imageData[i*4+1], imageData[i*4+2], imageData[i*4+3]];
        }
        chunks[x][z]=pixelArray;
        //TODO obsolete pixels
        queueSpace++;
        if(queueSpace > 0 && queue.length > 0){
            dl(queue.pop());
        }
    }
    image.onerror = function() {
        emptyChunks.push(chunk);
        queueSpace++;
        if(queueSpace > 0 && queue.length > 0){
            dl(queue.pop());
        }
    }
    image.src = "chunks/"+x+"_"+z+".png?"+Date.now();
}

var screenUpdate = function(){
    for(var x=viewx; x<viewx+viewWidth; x++){
        for(var y=viewy; y<viewy+viewHeight; y++){
            console.log(x, y);
            var color = get(x, y);
            context.fillStyle= "rgba("+color[0]+","+color[1]+","+color[2]+","+color[3]+")";
            context.fillRect(x-viewx, y-viewy, 1, 1);
        }
    }
}

context.fillRect(3, 20, 40, 40);
screenUpdate();

/* pointless

var put = function(imageData, x, y) { //shorthand
    //context.putImageData(imageData, x, y);
    cache[x][y]=imageData;
}
*/

/* probably pointless
var get = function(x, y){
    return context.getImageData(x, y, 1, 1);
}

var getRectangle = function(x, y, width, height){
    var rectangle = new Array();
    for (y; y<y+height; y++) {
        rectangle
    }
}
*/

//context.putImageData(imageData, x, y);

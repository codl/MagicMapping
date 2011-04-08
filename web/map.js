var toggleInfo = function() {
    var info = document.getElementById("info");
    var ps = info.childNodes;
    console.log(ps);
    for( var i = 2; i < ps.length; i++) {
        if (ps[i].style != undefined) {
            if (ps[i].style.display == "none") {
                ps[i].style.display = "block"
            } else {
                ps[i].style.display = "none"
            }
        }
    }
}
var loadchunk = function(x, z) {
    var div = document.getElementById(x + " "+ z);
    if (div == null) {
        div = document.createElement("div");
        div.id = x + " "+ z;
        div.style.backgroundImage = "url('chunks/"+x+"_"+z+".png?"+Date.now()+"')";
        div.style.position = "absolute";
        div.style.height = 864;
        div.style.width = 191;
        div.style.left = (x + z) * 192/2;
        div.style.top = (z-x)*48;
        div.style.zIndex = z-x;
        var chunks = document.getElementById("chunks");
        chunks.appendChild(div);
    }
}

var updateChunk = function(x, z) {
    var div = document.getElementById(x + " "+ z);
    if (div != null) {
        //TODO xhr queue?
        div.style.backgroundImage = "url('chunks/"+x+"_"+z+".png?"+Date.now()+"')";
    }
}

var go = function(x, y, z) {
    var chunks = document.getElementById("chunks");
    marginLeft = -((x+z)*6) + window.innerWidth/2;
    marginTop = -((128-y)*6 + (z-x)*3 + 16*3) + window.innerHeight/2;
    loadchunks(Math.floor(x/16), Math.floor(z/16));
    chunks.style.marginLeft = marginLeft;
    chunks.style.marginTop = marginTop;
}

var loadchunks = function(x, z) {
    for (i=x-1; i<=x+1; i++){
        for (j=z-1; j<=z+1; j++){
            loadchunk(i, j);
        }
    }
    var limit = Math.floor(Math.max(window.innerHeight/2/48, window.innerWidth/2/96));
    for (i=x-limit; i<=x+limit; i++){
        for (j=z-limit; j<=z+limit; j++){
            loadchunk(i, j);
        }
    }
}

var getCenterChunk = function() {
    var y = Math.floor((marginTop + window.innerHeight/2)/48);
    var x = Math.floor(-(marginLeft - window.innerWidth/2)/96);
    var cx = x;
    var cz;
    cz = (cx-y)/2;
    cx-=cz;
    return [Math.floor(cx), Math.floor(cz)];
}

var checkUpdates = function(lastupdate) {
    recent = new XMLHttpRequest();
    recent.open("GET", "./recent");
    recent.send(null);
    recent.addEventListener('load', function(e){
        var text = recent.responseText;
        var lines = text.split("\n");
        for (var i = 0; i < lines.length - 1; i++) {
            line = lines[i].split(" ");
            if(line[0].valueOf() > lastupdate) {
                updateChunk(line[1].valueOf(), line[2].valueOf());
                console.log(line);
            }
        }
        window.setTimeout(checkUpdates, 1000*10, Date.now());
    }, false);

}


var marginLeft;
var marginTop;
var prevx;
var prevy;
window.onload = function() {
    var chunks = document.getElementById("chunks");
    var viewport = document.getElementById("viewport");
    viewport.onmousedown = function(e) {
        e.preventDefault();
        prevx = e.clientX;
        prevy = e.clientY;
        if (e.button == 0) {
            viewport.onmousemove = function(e) {
                e.preventDefault();
                marginLeft += e.clientX - prevx;
                marginTop += e.clientY - prevy;
                chunks.style.marginLeft = marginLeft;
                chunks.style.marginTop = marginTop;
                prevx = e.clientX;
                prevy = e.clientY;
                var center = getCenterChunk();
                var x = center[0];
                var z = center[1];
                loadchunks(x, z);
    /*            var children = chunks.childNodes; // Shows which chunk is in the center
                for (var i = 0; i < children.length; i++) {
                    children[i].style.opacity = 1;
                }
                var div = document.getElementById(x+" "+z);
                if( div!= null) {
                    div.style.opacity = 0.5;
                }*/
            }
        }
    }
    viewport.onmouseup = function(e) {
        viewport.onmousemove = null;
    }
    go(8, 64, 8);
    checkUpdates(Date.now());
}


$(document).ready(	
	//ocultar botones de ayuda
	function() {
		document.getElementById("btn-left").style.display = "none";
 		document.getElementById("btn-right").style.display = "none";
 		document.getElementById("btn-close").style.display = "none";
 		document.getElementById("closeHelp").style.display = "none";
  	 $(".loader").fadeOut("slow");
	}
);
//Variables 
var contador = 1;
var helpConter = 1;
var countZoom = 1;
var zoom = 1.0;
var time = 4000;
var color = "red";


//ocultar y mostrar menu al dar click en la barra de menu
function main(viewHelp){


	//Click Imagen menu
	$('.imgMenu').click(function(){
		
		if(contador == 1){
	
			$('nav').animate({
				left: '0'
			});
			
			contador = 0;
			
		} else {		
			contador = 1;
			$('nav').animate({
				left: '-100%'
				
			});	
		}
 
	});	
	//Acciones de la ayuda
	$('.helpImg').click(function(){
		//Alert
		alertify.confirm('',"¿Mostrar ayuda?",
			//Aceptar
 		 	function() { 
 		 	
				//Habilitar botones de ayuda
				document.getElementById("btn-left").style.display = "block";
 				document.getElementById("btn-right").style.display = "block";
 				document.getElementById("btn-close").style.display = "block";
 				document.getElementById("menuLabel").style.display = 'inline';
 				
 				//Oculatar boton ayuda
 				document.getElementById("helpImg").style.display = 'none';
 				//Mostrar la primer ayuda
				document.getElementById("menuLabel").innerHTML = "Menú";			
				document.getElementById("imgMenu").style.border = '1px solid '+color;
				document.getElementById("imgMenu").style.boxShadow = '0 0 12px 0 '+color; 
				document.getElementById("imgMenu").style.boxShadow = '0 0 12px 0 '+color;  			
				
				//bloquear las acciones
				blockElements(true);
						
    		 },
    		 //no aceptar
 	     	 function() { 
 	     	 	
 	     	 }
		).set('labels', {ok:'Si', cancel:'No'}).set('closable', true).set('notifier','position', 'bottom-center');

 
	});	
	//Boton anterior
	$('.btn-float-left').click(function(){
		//primer ayuda
		if(helpConter == 1){
		
			document.getElementById("filters").style.border = '0'
    	  	document.getElementById("filters").style.boxShadow = '0 0 0px 0 '+color;
			document.getElementById("menuLabel").style.display = 'inline';
			document.getElementById("menuLabel").innerHTML = "Menú";			
			document.getElementById("imgMenu").style.border = '1px solid '+color;
			document.getElementById("imgMenu").style.boxShadow = '0 0 12px 0 '+color; 
			document.getElementById("sectionTable").style.border = '0';
    	 	document.getElementById("sectionTable").style.boxShadow = '0 0 0px 0 '+color; 
    	 	
			//segunda ayuda
		}else if(helpConter == 2){
		
			document.getElementById("menuLabel").innerHTML = "Pestañas";
    	 	document.getElementById("sectionTable").style.border = '1px solid '+color;
    	 	document.getElementById("sectionTable").style.boxShadow = '0 0 12px 0 '+color; 
	   	    document.getElementById("imgMenu").style.border = '0';
	   	  	document.getElementById("imgMenu").style.boxShadow = '0 0 0px 0 '+color; 
	   	  	document.getElementById("filters").style.border = '0';
    	  	document.getElementById("filters").style.boxShadow = '0 0 0px 0 '+color; 
    	  	
			helpConter--;
			//tercer ayuda
		}else if(helpConter == 3 ){
		
			document.getElementById("menuLabel").innerHTML = "Filtros"; 
			document.getElementById("sectionTable").style.border = '0';
    	  	document.getElementById("sectionTable").style.boxShadow = '0 0 0px 0 '+color; 
    	    document.getElementById("filters").style.border = '1px solid '+color;
    	    document.getElementById("filters").style.boxShadow = '0 0 12px 0 '+color; 
    	  	document.getElementById("frame").style.border = '0';
    	  	document.getElementById("frame").style.boxShadow = '0 0 0px 0 '+color; 
    	  	
			helpConter--;
		}
			
		
	});
	//Boton cerrar 
	$('.btn-float-close').click(function(){
	
		document.getElementById("btn-left").style.display = "none";
 		document.getElementById("btn-right").style.display = "none";
 		document.getElementById("btn-close").style.display = "none";
		document.getElementById("frame").style.border = '0';
		document.getElementById("menuLabel").style.display = 'none';
		document.getElementById("frame").style.boxShadow = '0 0 0px 0 '+color; 
		document.getElementById("helpImg").style.display = "inline";
		document.getElementById("divIndex").style.pointerEvents = 'auto';
		document.getElementById("imgMenu").style.border = '0';
		document.getElementById("imgMenu").style.boxShadow = '0 0 0px 0 '+color; 
		document.getElementById("sectionTable").style.border = '0';
    	document.getElementById("sectionTable").style.boxShadow = '0 0 0px 0 '+color; 
    	document.getElementById("filters").style.border = '0';
    	document.getElementById("filters").style.boxShadow = '0 0 0px 0 '+color; 
    	
    	//Desbloquear acciones
    
    	blockElements(false);
    	
		helpConter = 1;
				 			
	});
	//Boton siguiente
	$('.btn-float-right').click(function(){

		if(helpConter == 1){
			 	
			document.getElementById("menuLabel").innerHTML = "Pestañas";
    	 	document.getElementById("sectionTable").style.border = '1px solid '+color;
    	 	document.getElementById("sectionTable").style.boxShadow = '0 0 12px 0 '+color; 
	   	  	document.getElementById("imgMenu").style.border = '0';
	   	  	document.getElementById("imgMenu").style.boxShadow = '0 0 0px 0 '+color; 
	   	  		
	   	  	helpConter++;
		}else if(helpConter == 2){
		
			document.getElementById("menuLabel").innerHTML = "Filtros"; 
    	  	document.getElementById("sectionTable").style.border = '0';
    	  	document.getElementById("sectionTable").style.boxShadow = '0 0 0px 0 '+color; 
    	  	document.getElementById("filters").style.border = '1px solid '+color;
    	  	document.getElementById("filters").style.boxShadow = '0 0 12px 0 '+color; 
    	  	
    	  	helpConter++;	    	  		
		
		}else if(helpConter == 3){
		
			document.getElementById("menuLabel").innerHTML = "Información";    
    	  	document.getElementById("filters").style.border = '0';
    	  	document.getElementById("filters").style.boxShadow = '0 0 0px 0 '+color; 
    	  	document.getElementById("frame").style.border = '1px solid '+color;
    	  	document.getElementById("frame").style.boxShadow = '0 0 12px 0 '+color; 
				 	
				 	
    	  //	helpConter++;	   	
		}
	});

}

//ocultar menu al dar click afuera de el menu

 $(document).on("click",function(e) {
                    
         var container = $(".menu_bar");
                            
            if (!container.is(e.target) && container.has(e.target).length === 0) { 
               contador = 1;
				$('nav').animate({
					left: '-100%'
				});  
				//setTimeout(function() { 
				//	document.getElementById("divFrame").style.overflow = 'scroll';
				//},500); 
            }
    });
 //ocultar menu
 function inicia(){

 	  var xIni;
		    var yIni;
		    var canvas = document.getElementById('menu');
		    //cunado se hace touch
		    canvas.addEventListener('touchstart', function(e){
		        if (e.targetTouches.length == 1) { 
		   			 var touch = e.targetTouches[0]; 
		    			xIni = touch.pageX;
		    			yIni = touch.pageY;
		 		}
		    }, false);
			//al cambiar dispositivo de orientacion
		    window.addEventListener("orientationchange", function() {			
				
				if(window.orientation == 90 || window.orientation == -90 ){
					contador = 1;
					$('nav').animate({
						left: '-100%'
					});  
					
				}
				if(window.orientation == 0 ){	
					contador = 1;			
					$('nav').animate({
						left: '-100%'								
					});     
					
				}
			}, false);
			//al mover haciendp click
		    canvas.addEventListener('touchmove', function(e){
		        if (e.targetTouches.length == 1) { 
		    		var touch = e.targetTouches[0]; 
		    		if((touch.pageX > xIni+0) && (touch.pageY > yIni-5) && (touch.pageY < yIni+5)){
					
		   	 		}		    
		    		if((touch.pageX<xIni-60) && (touch.pageY> yIni-5) && (touch.pageY < yIni+5)){
		    				var container = $(".menu_bar");          
 		           		if (!container.is(e.target) && container.has(e.target).length === 0) {  
 		           		       	
 		           		       contador = 1;
									$('nav').animate({
										left: '-100%'
							   });
							   
								  
 		           		 }
		    		} 
		 		}
		 		
		    }, false);  
 }
 
//cambiar de programa al delizar y zoom
 function iniciaFrame(i){
	  var x;	
	  var y;
 	  var xIni;
	  var yIni;
	  var xIni2;
	  var yIni2;


		    var canvas = document.getElementById('table');
		    canvas.addEventListener('touchstart', function(e){
		        if (e.touches.length == 1) { 
		   			 var touch = e.touches[0]; 
		    			xIni = touch.pageX;
		    			yIni = touch.pageY;
		 		}
		 		if(e.touches.length == 2){
		 			 var touch = e.touches[0]; 
		 			  var touch2 = e.touches[1];
		    			xIni = touch.pageX;
		    			yIni = touch.pageY;
		    			xIni2 = touch2.pageX;
		    			yIni2 = touch2.pageY;
		    			x = xIni - xIni2;
		    			y = yIni - yIni2;
		    			//si es negativo convertir a positivo
		    			if (x < 0){
		    				x = x * -1;
		    			}
		    			if (y < 0){
		    				y = y * -1;
		    			}	 		 
		 		}		 			 				 			
		 		
		    }, false);
		    //derecha
		    canvas.addEventListener('touchmove', function(e){
		  
		        if (e.touches.length == 1) { 
		    		var touch = e.touches[0]; 
		    		if((touch.pageX > xIni+120) && (touch.pageY > yIni-30) && (touch.pageY < yIni+30)){
						//contratos
						if(i == 1){
		            	 	parent.window.location='portal_start_mobile.jsp?program=racc&viewHelp=0';
		            	}
		            	//Cobranza
		            	if(i == 2){
		            		parent.window.location='portal_start_mobile.jsp?program=prty&viewHelp=0';		            	
		            	}
		            	//Inmueble
		            	if(i == 3){
		            		parent.window.location='portal_start_mobile.jsp?program=prrt&viewHelp=0';
		            	}
		            	
		   	 		}	
		   	 		//izquierda	    
		    		if((touch.pageX<xIni-120) && (touch.pageY> yIni-30) && (touch.pageY < yIni+30)){
						//contratos
		    			if(i == 1){
     					 	parent.window.location='portal_start_mobile.jsp?program=prty&viewHelp=0';
		    			}
		    			//Cobranza
		    			if(i == 2){		    				
		            		parent.window.location='portal_start_mobile.jsp?program=prrt&viewHelp=0';		            		
		            	}
		            	//Inmueble
		    			if(i == 3){
		            		parent.window.location='portal_start_mobile.jsp?program=racc&viewHelp=0';
		            	}
		            	
		    		} 
		    		
		 		}
		 		////////////////////////ZOOM 
		 		 if (e.touches.length == 2) { 
		 		 	var xFinal;
		 		 	var yFinal;
		 		 	var xFinal2;
		 		 	var yFinal2;
		 		 	var touch = e.touches[0]; 
		 		 		xFinal = touch.pageX;
		 		 		yFinal = touch.pagey;
		 		 	var touch2 = e.touches[1]; 
		 		 		xFinal2 = touch2.pageX;
		 		 		yFinal2 = touch2.pagey;
		 		 		
		 		 		xFinal = xFinal - xFinal2;
		 		 		yFinal = yFinal - yFinal2;
		 		 		if(xFinal < 0){
		 		 			xFinal = xFinal * -1;
		 		 		}
		 		 		if(yFinal < 0){
		 		 			yFinal = yFinal * -1;
		 		 		}
		 		 		if(x > xFinal || y > yFinal ){
		 		 			if(zoom > 1.0){
		 		 				zoom = zoom - 0.015;
//		 		 				document.getElementById("table").style.transform = "scale("+zoom+")"; 
		 		 				if (navigator.appVersion.indexOf("Android")!=-1){
		 		 					document.getElementById("table").style.zoom = zoom;
		 		 				}
		 		 			}		 		 			
		 		 		}
		 		 		if(x < xFinal || y < yFinal ){
		 		 			if(zoom < 2.0){
		 		 				zoom = zoom + 0.015;
		 		 				if (navigator.appVersion.indexOf("Android")!=-1){
		 		 					document.getElementById("table").style.zoom = zoom;
		 		 				}
//		 		 				document.getElementById("table").style.transform = "scale("+zoom+")"; 
		 		 			}
		 		 			
		 		 		} 		 
		  		 } 	
		 				 	
		    }, false); 	    		 				     
 } 
 //bloquear/debloquear elementos
 function blockElements(action){
 	if(action){
 		
 		document.getElementById("imgMenu").style.pointerEvents = 'none';
		document.getElementById("sectionTable").style.pointerEvents = 'none';
		document.getElementById("filters").style.pointerEvents = 'none';
		document.getElementById("frame").style.pointerEvents = 'none';
		
 	}else{
 		
 	    document.getElementById("imgMenu").style.pointerEvents = 'auto';
		document.getElementById("sectionTable").style.pointerEvents = 'auto';
		document.getElementById("filters").style.pointerEvents = 'auto';
    	document.getElementById("frame").style.pointerEvents = 'auto';
    	
 	}
 }
 //confirmacion para salir
function logout(){	
		alertify.confirm('',"¿Esta seguro que desea salir?",
 		 	function() {
   				 window.location.replace("portal_logout.jsp");
  			 },
 	     	 function() { }
		).set('labels', {ok:'Si', cancel:'No'}).set('closable', false);
}     
exercise



# Encapsulate collection

We have the following code:
```
function Order() {
         this.orderLines = [];
         this.orderTotal = 0;
}
Order.prototype.getOrderLines = function() {
         return this.orderLines;
};
Order.prototype.addOrderLine = function(orderLine) {
         this.orderTotal += orderLine.total;
         this.orderLines.push(orderLine);
};
Order.prototype.removeOrderLine = function(orderLineItem) {
         var orderTotal, orderLine;
         orderLine = this.orderLines.map(function(order) {
                  return order === orderLineItem;
         })[0];

         if(typeof orderLine === 'undefined' || orderLine === null) {
                  return;
         }
         this.orderTotal -= orderLine.total;
         this.orderLines.splice(this.orderTotal, 1);
};
```
```
var order = new Order();
order.addOrderLine( { total: 10 } );
console.log(order.getOrderLines());  // [ { total: 10 } ]
console.log(order.orderTotal);   // 10
```
The problem with this code is that anyone could get access to orderLines and add or modify values without increasing or decreasing orderTotal.
```
order.orderLines[0] = { total: 20 };
console.log(order.getOrderLines()); // [ { total: 20 } ]
console.log(order.orderTotal);  // 10;
```

---
Modify the code to encapsulate the collection to avoid this issue.

```js
function Order() {
         this.orderLines = [];
         this.orderTotal = 0;
}
Order.prototype.getOrderLines = function() {
         return this.orderLines;
};
Order.prototype.addOrderLine = function(orderLine) {
         this.orderTotal += orderLine.total;
         this.orderLines.push(orderLine);
};
Order.prototype.removeOrderLine = function(orderLineItem) {
         var orderTotal, orderLine;
         orderLine = this.orderLines.map(function(order) {
                  return order === orderLineItem;
         })[0];

         if(typeof orderLine === 'undefined' || orderLine === null) {
                  return;
         }
         this.orderTotal -= orderLine.total;
         this.orderLines.splice(this.orderTotal, 1);
};
```

```js
function Order() {
         var orderLines, orderTotal;
         orderLines = [];
         orderTotal = 0;
         this.getOrderLines = function () {
            return orderLines;
         };
         this.getOrderTotal = function () {
            return orderTotal;
         };
         this.setOrderTotal = function (total) {
            orderTotal += total;
         };
}
Order.prototype.addOrderLine = function (orderLine) {
    var orderLines;
    orderLines = this.getOrderLines();
    this.setOrderTotal(orderLine.total);
    orderLines.push(orderLine);
};
Order.prototype.removeOrderLine = function(orderLineItem) {
         var orderTotal, orderLine, orderLines;
         orderLines = this.getOrderLines();
         orderLine = this.orderLines.map(function(order) {
                  return order === orderLineItem;
         })[0];

         if(typeof orderLine === 'undefined' || orderLine === null) {
                  return;
         }

         this.setOrderTotal( (-1 * orderLine.total) );
         orderLines.splice( this.getOrderTotal(), 1);
};
```

```js
var order = new Order();
assert(typeof order.orderLines === 'undefined');
```




# Input Search

Reviewing the code of your colleague you have found this snipped of code:

```
$( document ).ready( function() {
  $( '#inputSearch' ).keypress( function() {
      $.ajax( {
        url: 'http://www.domain.com/search',
        data: this.value,
        success: function ( data )
        {
            var results = data.results;
            $( '#list' ).empty();
            $.each( data, function ( item ) {
                $( '#list' ).append( '<li>' + item + '</li>' );
            } );

        },
        error: function ( xhr, status, error ) {
            console.log( 'Something goes wrong!', status, error.message );
        }
      } );
  } );
} );
```
In this code there is a performance issue that should be fixed, could you help us?

---
Fix the performance issue:
```js
$( document ).ready( function() {
  $( '#inputSearch' ).keypress( function() {
      $.ajax( {
        url: 'http://www.domain.com/search',
        data: this.value,
        success: function ( data )
        {
            var results = data.results;
            $( '#list' ).empty();
            $.each( data, function ( item ) {
                $( '#list' ).append( '<li>' + item + '</li>' );
            } );

        },
        error: function ( xhr, status, error ) {
            console.log( 'Something goes wrong!', status, error.message );
        }
      } );
  } );
} );
```
```js
function delayTimer(delay){
  var timer;
    return function(fn){
      timer = clearTimeout(timer);
      if(fn)
        timer = setTimeout(function() {
           fn();
        },delay);

      return timer;
  };
}
var delayer = delayTimer(500);

$( document ).ready( function() {
  $( '#inputSearch' ).keyup( function() {
      delayer(function() {
        var $list = $( '#list' );
        $.ajax( {
          url: 'http://www.domain.com/search',
          data: this.value,
          success: function ( data )
          {
              var results = data.results;
              $list.empty();
              $.each( data, function ( item ) {
                  $list.append( '<li>' + item + '</li>' );
              } );

          },
          error: function ( xhr, status, error ) {
              console.log( 'Something goes wrong!', status, error.message );
          }
        } );
      } );
  } );
} );
```

```js
assert(counter === 1);
```
```js
var document = '';
var counter = 0;
var setTimeout = function (cb) {
    cb();
};
var $ = function ( element ) {
    var jQuery = {
        ready: function ( callback ) {
            callback();
            return jQuery;
        },
        keypress: function ( callback ) {
            callback();
            return jQuery;
        },
        keyup: function ( callback ) {
            counter++;
            callback();
            return jQuery;
        }
    };
    return jQuery;
};
$.ajax = function () {};
```
---






# Point
This challenge needs you to implement a Point class so that the following code returns true.
```
new Point( new Point(10, 20) + new Point(30, 50) ).toString() === '{40,70}';
```

---
Implement Point and assume that:

* Must be generic and be able to handle x and y values from 0..999
* The requirements to do it are to implement valueOf and/or toString methods.

```js

```
```js
var Point = function (x, y) {
	if(typeof x === 'string') {
		this.convertStringToCoordinates(x);
	}else{
		this.x = x;
		this.y = y;
	}
};
Point.prototype.convertStringToCoordinates = function ( value ) {
	var arr, index, len, item;
	this.x = 0;
	this.y = 0;
	arr = JSON.parse('[' + value.replace(/{/g,'[').replace(/}/g,']').replace( new RegExp('\\]\\[', 'g'), '],[') + ']');
	len = arr.length;
	for(index = 0; index < len; index++) {
		item = arr[index];
		this.x += item[0];
		this.y += item[1];
	}
};
Point.prototype.toString = function () {
	return '{' + this.x + ',' + this.y + '}';
};
```
```js
assert(new Point( new Point(10, 20) + new Point(30, 50) ).toString() === '{40,70}');
```
---


function Check_Date( from_date, to_date ){  // 確認日期
    var from_bage_date=""; // 開始日期
    var from_chang_bage_date = from_date.split("/");
    var to_bage_date=""; // 結束日期
    var to_chang_bage_date = to_date.split("/");
    for(var i=0; i<from_chang_bage_date.length;++i) from_bage_date = from_bage_date + from_chang_bage_date[i];
    for(var i=0; i<to_chang_bage_date.length;++i) to_bage_date = to_bage_date + to_chang_bage_date[i];
    if( parseInt(from_bage_date) > parseInt(to_bage_date) ) {
      return false;
    } else {
      return true;
    }
}

function checkID( id ) {
   return true;
}

function checkID1( id ) {
   tab = "ABCDEFGHJKLMNPQRSTUVWXYZIO"
   A1 = new Array (1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3 );
   A2 = new Array (0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5 );
   Mx = new Array (9,8,7,6,5,4,3,2,1,1);

   if ( id.length != 10 ) return false;
   i = tab.indexOf( id.charAt(0) );
   if ( i == -1 ) return false;
   sum = A1[i] + A2[i]*9;

   for ( i=1; i<10; i++ ) {
      v = parseInt( id.charAt(i) );
      if ( isNaN(v) ) return false;
      sum = sum + v * Mx[i];
   }
   if ( sum % 10 != 0 ) return false;
   return true;
}

function check_numerical(dstText)
{
  data = dstText.match(/[^0-9]/g);
  if(data || !dstText){
    return false;
  }
  return true;
}

/********************** Check the Date a format **************/
function check_date_format(dstText)
{
  dstText = dstText.replace("/","-");
  dstText = dstText.replace("/","-");
  data = dstText.match(/(\d{4})\-(\d{2})\-(\d{2})/);
  if(data!=null) {
    return true;
  } else {
    return false;
  }
}

function Check_Input_Date(date){
  var flag = true;
  var yy, mm, dd;
  date = date.replace("/","");
  date = date.replace("/","");
  if (date.length==8) {
    yy = date.substring(0,4);
    mm = date.substring(4,6);
    dd= date.substring(6,8);
    if ( mm > 0 && mm < 13 && dd > 0 && dd < 32 ) {
      if (mm ==2) {
        if (( ( yy % 4 == 0) && ( yy % 100 != 0)) || ( yy % 400 == 0 )) {  // 潤月
          if (dd > 29) {
             flag = false;
          }
        } else {
          if (dd > 28) {
             flag = false;
          }
        }
      } else {
        if ( mm==1 || mm==3 || mm==5 || mm==7 || mm==8 || mm==10 || mm==12 ) {
          if (dd > 31) {
             flag = false;
          }
        } else {
          if (dd > 30) {
             flag = false;
          }
        }
      }
    } else {
      flag = false;
    }
  } else {
    flag = false;
  }
  return flag;
}


//npm init
//
//node db.js


/*const sql = require("mssql");
const sqlConfig = {
    user: "userTeam2",
    password: "KennwortTeam2",
    database: "ebos_Progress_Team2",
    server: "10.10.30.219",
    port: 50915,
    options: {
        encrypt: false, // for azure
        trustServerCertificate: true, // change to true for local dev / self-signed certs
    },
};*/
var reinfolge = ["TP 1", "TP 2", "QV 2", "TP 3", "TP 4", "QV 1", "TP 5", "TP 6", "QV 3", "TP 10", "QV 8", "TP 9", "TP 11", "QV 4", "TP 12", "TP 13", "TP 14", "QV 7", "TP 14.1", "TP 15", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "RBG", "TP 30", "TP 1"];

/*sql.connect(sqlConfig, (err) => {
    if (err) {
        throw err;
    }
    console.log("Connection Successful !");
    //### steht mpr lager
    //LG [regal (1-5)] | [etage (1-16)]*/
    pushifpossible2();
//});

function pushifpossible() {
    let result;
    for (let i = reinfolge.length - 2; i >= 0; i--) {
        let isEmpty2 = 0;
        let isEmpty = 0;
        let a = new Promise((res) => {
            var request = new sql.Request();
            request.input('lname', sql.VarChar, reinfolge[i])
            request.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                //console.dir(result)
                try {
                    isEmpty = result.recordset[0].PalNo
                        //console.dir("funktioniert")

                } catch (e) {
                    isEmpty = 0;
                    //console.dir(e)
                    res(0);
                }
                //console.dir(isEmpty + " is the value")
                res(isEmpty)
            })

        })
        a.then(
            function(value) {
                if (value != 0) {
                    console.dir("isvalid " + value)
                    console.dir("fffffffffffffffffffff")

                    let b = new Promise((res) => {
                        var request = new sql.Request();
                        request.input('lname', sql.VarChar, reinfolge[i + 1])
                        reest.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                            try {
                                isEmpty2 = result.recordset[0].PalNo
                            } catch (e) {
                                isEmpty2 = 0;
                                res(0)
                            }
                            res(isEmpty2)
                        })
                    })
                    b.then(
                        function(value2) {
                            console.dir("ggggggggggg")
                            if (value2 == 0) {
                                var request = new sql.Request();
                                request.input('myval', sql.VarChar, reinfolge[i])
                                request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,0,CURRENT_TIMESTAMP)', (err, result) => {})
                                var request = new sql.Request();
                                request.input('myval', sql.VarChar, reinfolge[i + 1])
                                request.input('palnumma', sql.Int, isEmpty);
                                request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,@palnumma,CURRENT_TIMESTAMP)', (err, result) => {})
                            }

                        },
                        function(err) {
                            console.dir("error 2");
                        }
                    )
                }
            },
            function(err) {
                console.dir("error");
            }
        )
    }

}
async function pushifpossible2(){
    for (let i = reinfolge.length - 2; i >= 0; i--) {
        let isEmpty2 = 0;
        let isEmpty = 0;
        
        console.dir(i+" -- "+reinfolge[i]+" -- "+reinfolge[i+1])
//        continue;

        await Promise((res) => {
            var request = new sql.Request();
            request.input('lname', sql.VarChar, reinfolge[i])
            request.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                try {
                    isEmpty = result.recordset[0].PalNo
                } catch (e) {
                    isEmpty = 0;
                    res(0)
                }
                res(isEmpty2)
            })
        })
        if(isEmpty!=0){
            await Promise((res) => {
                var request = new sql.Request();
                request.input('lname', sql.VarChar, function(){ if(reinfolge[i+1]=="###"){
                           for (var j=0;j<5;j++){
                               for(var k=0;k<16;k++){
                                request.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                                    try {
                                        isEmpty = result.recordset[0].PalNo
                                    } catch (e) {
                                        isEmpty = 0;
                                        res(0)
                                    }
                                    res(isEmpty2)
                                })
                               }
                           } 
                        }
                    })
                request.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                    try {
                        isEmpty2 = result.recordset[0].PalNo
                    } catch (e) {
                        isEmpty2 = 0;
                        res(0)
                    }
                    res(isEmpty2)
                })
            })
            if(isEmpty2==0){
                var request = new sql.Request();
                request.input('myval', sql.VarChar, reinfolge[i])
                request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,0,CURRENT_TIMESTAMP)', (err, result) => {})
                var request = new sql.Request();
                request.input('myval', sql.VarChar, reinfolge[i + 1])
                request.input('palnumma', sql.Int, isEmpty);
                request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,@palnumma,CURRENT_TIMESTAMP)', (err, result) => {})           
            }  
        }
    }
}
/*

        consodir("latest value at position" + isEmpty) if (isEmpty != 0) { // if currenposition has a palette try to push it
            console.dir("fffffffffffffff only once" + isEmpty)
            var request = new sql.Request();
            request.input('lname', sql.VarChar, reinfolge[i + 1])
            reest.query('select distinct PalNo from dbo.LocPalHistory where TimeStamp=(select max(TimeStamp) from dbo.LocPalHistory where LocationName=@lname) and LocationName=@lname', (err, result) => {
                    let isEmpty2 = 0; {
                        isEmpty2 = result.recordset[0].PalNo
                    } catch (e) {
                        isEmpty2 = 0;
                    }
                    console.dir(isEmpty2 + " is the next palette. last" + isEmpty + " " + palettennummer)
                    isEmpty2 == 0) {
                    //the current one is not empty and the next one is empty so i need ot push one ahead
                    var request = new sql.Request();
                    request.input('myval', sql.VarChar, reinfolge[i])
                    request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,0,CURRENT_TIMESTAMP)', (err, result) => {})
                    var request = new sql.Request();
                    request.input('myval', sql.VarChar, reinfolge[i + 1])
                    request.input('palnumma', sql.Int, isEmpty);
                    console.dir(isEmpty + " sollte etwas sein" + palettennummer)
                    request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,@palnumma,CURRENT_TIMESTAMP)', (err, result) => {})
                }
            })
    }

    })

    
}
});
console.dir("steafdadfa")
} */
/*sql.on("error", (err) => {
    // ... error handler
    console.log("Sql database connection error ", err);
});*/
/*var request = new sql.Request();
    request.input('myval', sql.VarChar, 'TP 1')
    request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,68,CURRENT_TIMESTAMP)', (err, result) => {
        console.dir(result)
    })*/
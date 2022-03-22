//npm init
//
//node db.js


const sql = require("mssql");
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
};
sql.connect(sqlConfig, (err) => {
    if (err) {
        throw err;
    }
    console.log("Connection Successful !");
    var reinfolge = ["TP 1", "TP 2", "QV 2", "TP 3", "TP 4", "QV 1", "TP 5", "TP 6", "QV 3", "TP 10", "QV 8", "TP 9", "TP 11", "QV 4", "TP 12", "TP 13", "TP 14", "QV 7", "TP 14.1", "TP 15", "QV 5", "TP 18", "TP 23", "TP 25", "RBG", "###", "RBG", "TP 30"];
    //### steht mpr lager
    //LG [regal (1-5)] | [etage (1-16)]
    pushifpossible();
});

function pushifpossible() {
    let result;
    new sql.Request().query("select * from dbo.LocPalHistory", (err, result) => {
        console.dir(result);
        console.dir(typeof(result));
        return;
        for (let i = 0; i < reinfolge.length; i++) {
            var request = new sql.Request();
            request.input('myval', sql.VarChar, 'TP 1')
            request.query('insert into dbo.LocPalHistory (LocationName,PalNo,Timestamp) values (@myval,68,CURRENT_TIMESTAMP)', (err, result) => {
                console.dir(result)
            })
        }
    });
    console.dir("steafdadfa")
}
sql.on("error", (err) => {
    // ... error handler
    console.log("Sql database connection error ", err);
});
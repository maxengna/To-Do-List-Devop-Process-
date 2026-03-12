require('./tracing.js')

const express = require('express');
const mysql = require('mysql2')  // change package to mysql2 to avoid "Error: ER_NOT_SUPPORTED_AUTH_MODE: Client does not support authentication protocol requested by server; consider upgrading MySQL client"
const cors = require('cors')

const app = express()
app.use(cors())
app.use(express.json());

const router = express.Router();


const db = mysql.createPool({
  host: process.env.DB_HOST || "mysql",   // ใช้ service name ใน k8s
  port: 3306,
  user: process.env.DB_USER || "demo",
  password: process.env.DB_PASSWORD || "maxdev789",
  database: process.env.DB_NAME || "todolist",
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// ทดสอบการเชื่อมต่อ DB ตอน start app
db.getConnection((err, connection) => {
  if (err) {
    console.error("❌ Database connection failed:", err.message);
  } else {
    console.log("✅ Connected to MySQL");
    connection.release();
  }
});



// const db = mysql.createConnection({
// //  use "mysql" & port:3306 to connect to the db in the mysql-container @ port 3306 through the docker-network created by the docker-compose.
// //  use "host.docker.internal" & port:3000 to connect to the db in the mysql-container @ port 3306 through localhost(docker-host), port: 3000.
//     host: "mysql", 
//     port: "3306" , 

//     user: "root",
//     password: "max1234", //"23690892"omar1234
//     database: "todolist" //todos-db
// })
 
app.get('/api/TodoList', (req,res)=>{
    const sql_query = "SELECT * FROM todolist.todoss";
    db.query(sql_query, (err , data)=>{
        if(err) return res.json(err);
        return res.json(data);
    })

})

app.post('/api/Add', (req,res)=> {
    const todo = req.body.todo
    const description  = req.body.description
    const deadline = req.body.deadline
    const priority = req.body.priority
    const status = req.body.status
    const tag = req.body.tag



    db.query('INSERT INTO todolist.todoss(Todo, Description, Deadline, Priority, Status, Tag ) VALUES(?,?,?,?,?,?)',
    [todo,description,deadline,priority,status,tag] , 
    (err,result) => {
        if (err)  {
            console.log(err)
            res.json({error: "Failed to add todo"})
        } else {
            res.send(" Added to Todo-List")
            console.log("Todo added successfully");
            console.log(req.body);
            res.json({message: "Todo added successfully", todo: req.body})
        }
    }
    );
});


app.delete('/api/Delete/:id', (req,res)=> {
  

    const delete_query = 'DELETE FROM todolist.todoss WHERE todoss.id = ?';

    db.query(delete_query, [req.params.id],

    (err,result) => {
        if (err)  {
            console.log(err)
            res.json({error: "Failed to delete todo"})
        } else {
            res.json({message: "Todo deleted successfully", id: req.params.id})
            console.log("Todo deleted successfully - ID:" ,req.params.id ) 
        }
    }
    );
});

app.use('/api', router)

app.get('/', (req,res)=>{
    return res.json("Backend");
})

app.listen(8081 ,'0.0.0.0', ()=> {
    console.log("Listening on port 8081")
})
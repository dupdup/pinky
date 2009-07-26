package org.pinky.code.util.jdbc

import collection.mutable.Map 
import collection.mutable.ListBuffer
import java.sql.{DriverManager, Connection, ResultSet, PreparedStatement, Statement, Date };
/**
 * based on http://scala.sygneca.com/code/simplifying-jdbc
 */
object Predef {
  private def strm[X](f: RichResultSet => X, rs: ResultSet): Stream[X] =
        if (rs.next) Stream.cons(f(new RichResultSet(rs)), strm(f, rs))
        else { rs.close(); Stream.empty };


    implicit def query[X](s: String, f: RichResultSet => X)(implicit stat: Statement) = {
        strm(f,stat.executeQuery(s));
    }

    implicit def conn2Statement(conn: Connection): Statement = conn.createStatement;

    implicit def rrs2Boolean(rs: RichResultSet) = rs.nextBoolean;
    implicit def rrs2Byte(rs: RichResultSet) = rs.nextByte;
    implicit def rrs2Int(rs: RichResultSet) = rs.nextInt;
    implicit def rrs2Long(rs: RichResultSet) = rs.nextLong;
    implicit def rrs2Float(rs: RichResultSet) = rs.nextFloat;
    implicit def rrs2Double(rs: RichResultSet) = rs.nextDouble;
    implicit def rrs2String(rs: RichResultSet) = rs.nextString;
    implicit def rrs2Date(rs: RichResultSet) = rs.nextDate;

    implicit def resultSet2Rich(rs: ResultSet) = new RichResultSet(rs);
    implicit def rich2ResultSet(r: RichResultSet) = r.rs;

    //resultset
    class RichResultSet(val rs: ResultSet) {
      var pos = 1
      def apply(i: Int) = { pos = i; this }
      def nextBoolean: Boolean = { val ret = rs.getBoolean(pos); pos = pos + 1; ret }
      def nextByte: Byte = { val ret = rs.getByte(pos); pos = pos + 1; ret }
      def nextInt: Int = { val ret = rs.getInt(pos); pos = pos + 1; ret }
      def nextLong: Long = { val ret = rs.getLong(pos); pos = pos + 1; ret }
      def nextFloat: Float = { val ret = rs.getFloat(pos); pos = pos + 1; ret }
      def nextDouble: Double = { val ret = rs.getDouble(pos); pos = pos + 1; ret }
      def nextString: String = { val ret = rs.getString(pos); pos = pos + 1; ret }
      def nextDate: Date = { val ret = rs.getDate(pos); pos = pos + 1; ret }

      def foldLeft[X](init: X)(f: (ResultSet, X) => X): X = rs.next match {
        case false => init
        case true => foldLeft(f(rs, init))(f)
      }
      def map[X](f: ResultSet => X) = {
        var ret = List[X]()
        while (rs.next())
        ret = f(rs) :: ret
        ret.reverse; // ret should be in the same order as the ResultSet
      }
    }

    implicit def ps2Rich(ps: PreparedStatement) = new RichPreparedStatement(ps);
    implicit def rich2PS(r: RichPreparedStatement) = r.ps;
    //prepared statement
    class RichPreparedStatement(val ps: PreparedStatement) {
      var pos = 1;
      private def inc = { pos = pos + 1; this }

      def query[X](params:AnyRef*)(f: RichResultSet => X): Stream[X] = {
	      setStatementParams(params)
        query(f)
      }

      def query[X](f: RichResultSet => X): Stream[X] = {
	      pos = 1; strm(f, ps.executeQuery)
      }

      def query:List[Map[String,AnyRef]] = {
        var l=  new ListBuffer[Map[String,AnyRef]]
        
         val rs = ps.executeQuery
        while (rs.next){
          //TODO:populate resultset
          //val rsmd = rs.getMetaData()
          //for (i <- rsmd.getColumnCount) {
          //
          //}
        }
        return null
      }

      private def setStatementParams(params:AnyRef*) = {
        var pos = 1
        for (param<-params) {
          ps.setObject(pos,param)
          pos +=1
        }
      }
      def queryWith(params:AnyRef*):List[Map[String,AnyRef]] = {
        setStatementParams(params)
        query
      }

      def executeWith(params:AnyRef*){
         var pos = 1
        for (param<-params) {
          ps.setObject(pos,param)
          pos +=1
        }
        ps.execute
      }

      def execute = { pos = 1; ps.execute }

      def << (b: Boolean) = { ps.setBoolean(pos, b); inc }
      def << (x: Byte) = { ps.setByte(pos, x); inc }
      def << (i: Int) = { ps.setInt(pos, i); inc }
      def << (x: Long) = { ps.setLong(pos, x); inc }
      def << (f: Float) = { ps.setFloat(pos, f); inc }
      def << (d: Double) = { ps.setDouble(pos, d); inc }
      def << (o: String) = { ps.setString(pos, o); inc }
      def << (x: Date) = { ps.setDate(pos, x); inc }
    }

    implicit def conn2Rich(conn: Connection) = new RichConnection(conn);
    //connection
    class RichConnection(val conn: Connection) {
      //statements
      def execute (sql: String):Unit = new RichStatement(conn.createStatement) execute sql;
      def execute (sql: Seq[String]):Unit = new RichStatement(conn.createStatement) execute sql;
      def query[X](s: String, f: RichResultSet => X): Stream[X] =  new RichStatement(conn.createStatement) query(s,f)
      //prepared statements
      def execute (sql:String,params:AnyRef*)= new RichPreparedStatement(conn.prepareStatement(sql)).executeWith(params)
      def query (sql:String,params:AnyRef*):List[Map[String,AnyRef]] = new RichPreparedStatement(conn.prepareStatement(sql)).queryWith(params)
      def query[X](sql:String,params:AnyRef*)(f: RichResultSet => X): Stream[X] = new RichPreparedStatement(conn.prepareStatement(sql)).query(params)(f)
    }

    implicit def st2Rich(s: Statement) = new RichStatement(s);
    implicit def rich2St(rs: RichStatement) = rs.s;

    //statement
    class RichStatement(val s: Statement) {
      def execute (sql: String):Unit = { s.execute(sql); this }
      def execute (sql: Seq[String]):Unit = { for (val x <- sql) s.execute(x); this }
      def query[X](sql: String, f: RichResultSet => X): Stream[X]  = {strm(f,s.executeQuery(sql))}
    }
}

package challenge.rf.api

/* STATE MACHINE*/
sealed trait State { def name : String}
case object INIT     extends State { val name = "INIT" }
case object STARTING extends State { val name = "STARTING" }
case object RUNNING  extends State { val name = "RUNNING" }
case object STOPPING extends State { val name = "STOPPING" }
case object DEAD     extends State { val name = "DEAD" }

/* Operation Result logic for OK/NOK Boot */
sealed trait Result
case object OK extends Result
case object NOK extends Result

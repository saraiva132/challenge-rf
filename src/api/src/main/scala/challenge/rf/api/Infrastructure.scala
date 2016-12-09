package challenge.rf.api

/* STATE MACHINE*/
sealed abstract class State(name : String)
case object NEW      extends State("NEW")
case object STARTING extends State("STARTING")
case object RUNNING  extends State("RUNNING")
case object STOPPING extends State("STOPPING")
case object DEAD     extends State("DEAD")

/* Operation Result logic for OK/NOK Boot */
sealed trait Result
case object OK extends Result
case object NOK extends Result

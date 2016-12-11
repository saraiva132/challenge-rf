package challenge.rf.api

sealed abstract class State(name : String)
/* Service has never been started*/
case object NEW      extends State("NEW")
/* Service is starting. Transiting to Running State*/
case object STARTING extends State("STARTING")
/* Service is Running*/
case object RUNNING  extends State("RUNNING")
/* Service is stopping. Transiting to DEAD State*/
case object STOPPING extends State("STOPPING")
/* Service is Dead. Meaning it already exists but is dormant.*/
case object DEAD     extends State("DEAD")
/* Due to irreversible error this service is disabled.*/
case object DISABLED extends State("DISABLED")

/* Operation Result logic for OK/NOK */
/** This trait had the finally to later include business logic that could allow: *
  * -> Disabling services that are not being able to start and returning error.
  * -> Retry profiles based on error.
  * */
sealed trait Result
case object OK extends Result
case object NOK extends Result
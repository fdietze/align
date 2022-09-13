package example.lambda

import scala.scalajs.js
import cats.data.Kleisli
import cats.effect.IO
import example.api.{EventApi, JoinInfo, RpcApi}
import funstack.backend.Fun
import funstack.lambda.apigateway
import sloth.Client
import facade.amazonaws.AWSConfig
import facade.amazonaws.services.chime.{Chime, CreateAttendeeRequest, CreateMeetingRequest, ListMeetingsRequest}

import chameleon.ext.circe._

object RpcApiImpl {
  private val chime = new Chime(AWSConfig.apply(region = "us-east-1"))
}

class RpcApiImpl(request: apigateway.Request) extends RpcApi[IO] {
  import RpcApiImpl._

  private val client     = Client.contra(Fun.ws.sendTransport[String])
  private val streamsApi = client.wire[EventApi[Kleisli[IO, *, Unit]]]

  def listMeetings = {
    val meetings = IO.fromFuture(IO(chime.listMeetingsFuture(ListMeetingsRequest())))

    meetings.map(_.Meetings.get.map(meeting => js.JSON.stringify(meeting)).toList)
  }

  def createMeeting = {
    val meeting = IO.fromFuture(IO(chime.createMeetingFuture(CreateMeetingRequest(null))))

    meeting.map(response => js.JSON.stringify(response.Meeting.get))
  }

  def joinMeeting(userName: String, meetingId: String) = {
    val attendee = IO.fromFuture(
      IO(
        chime.createAttendeeFuture(
          CreateAttendeeRequest(
            ExternalUserId = userName,
            MeetingId = meetingId,
          ),
        ),
      ),
    )

    attendee.map(response => js.JSON.stringify(response.Attendee.get))
  }

  override def join(string: String): IO[JoinInfo] = {
    // is there a meeting with same string that has a free spot?
    // if yes: add attendee, return meeting + attendee
    // else: create new meeting, return meeting + attendee
    ???
  }
}
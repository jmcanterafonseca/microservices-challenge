

package object mail {

  implicit def stringToSeq(single: String): Seq[String] = Seq(single)

  implicit def liftToOption[T](t: T): Option[T] = Some(t)

  private var mailParams: MailParams = null

  def init(params: MailParams) = {
    mailParams = params
  }

  sealed abstract class MailType

  case class Mail(
                   from: (String, String), // (email -> name)
                   to: Seq[String],
                   cc: Seq[String] = Seq.empty,
                   bcc: Seq[String] = Seq.empty,
                   subject: String,
                   message: String,
                   richMessage: Option[String] = None,
                   attachments: Seq[(java.io.File)] = Seq.empty
                 )

  case object Plain extends MailType

  case object Rich extends MailType

  case object MultiPart extends MailType

  object send {
    def a(mail: Mail) {
      import org.apache.commons.mail._

      val format =
        if (mail.attachments.nonEmpty) MultiPart
        else if (mail.richMessage.isDefined) Rich
        else Plain

      val commonsMail: Email = format match {
        case Plain => new SimpleEmail().setMsg(mail.message)
        case Rich => new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
        case MultiPart => {
          val multipartEmail = new MultiPartEmail()
          mail.attachments.foreach { file =>
            val attachment = new EmailAttachment()
            attachment.setPath(file.getAbsolutePath)
            attachment.setDisposition(EmailAttachment.ATTACHMENT)
            attachment.setName(file.getName)
            multipartEmail.attach(attachment)
          }
          multipartEmail.setMsg(mail.message)
        }
      }

      // Can't add these via fluent API because it produces exceptions
      mail.to foreach (commonsMail.addTo(_))
      mail.cc foreach (commonsMail.addCc(_))
      mail.bcc foreach (commonsMail.addBcc(_))

      // gmail config
      commonsMail.setHostName(mailParams.server)
      commonsMail.setAuthentication(mailParams.user, mailParams.pass)
      commonsMail.setSSLOnConnect(true)
      commonsMail.setSmtpPort(465)

      commonsMail.
        setFrom(mail.from._1, mail.from._2).
        setSubject(mail.subject).
        send()
    }
  }

}
package com.oczeretko.dsbforsinket

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.{Config, ConfigFactory}

class SettingsImpl(config: Config, secretsConfig : Config) extends Extension {
  val runOnWeekend: Boolean = config.getBoolean("dsbforsinket.run-on-weekend")
  val useFakeNotificationHub: Boolean = config.getBoolean("dsbforsinket.fake-notification-hub")

  val fakeNotificationHubBaseUrl : String  = secretsConfig.getString("dsbforsinket.secrets.fakeNotificationHub.baseUrl")
  val notificationHubConnectionString : String  = secretsConfig.getString("dsbforsinket.secrets.notificationHub.connectionString")
  val notificationHubName : String  = secretsConfig.getString("dsbforsinket.secrets.notificationHub.name")
}

object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {

  override def lookup = Settings

  override def createExtension(system: ExtendedActorSystem) =
    new SettingsImpl(system.settings.config, ConfigFactory.load("secrets"))

  override def get(system: ActorSystem): SettingsImpl = super.get(system)
}
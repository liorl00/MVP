package mvp.crypto

import java.security._
import org.bouncycastle.jce.ECNamedCurveTable
import akka.util.ByteString
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec

object ECDSA {

  def createKeyPair: KeyPair = {
    val ecSpec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("prime192v1")
    val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC")
    keyPairGenerator.initialize(ecSpec, new SecureRandom())
    keyPairGenerator.generateKeyPair
  }

  def sign(privateKey: PrivateKey, messageToSign: ByteString): ByteString = {
    val ecdsaSign: Signature = Signature.getInstance("SHA256withECDSA", "BC")
    ecdsaSign.initSign(privateKey)
    ecdsaSign.update(messageToSign.toArray)
    ByteString(ecdsaSign.sign)
  }

  def verify(signature: ByteString, message: ByteString, publicKey: PublicKey): Boolean = {
    val ecdsaVerify: Signature = Signature.getInstance("SHA256withECDSA", "BC")
    ecdsaVerify.initVerify(publicKey)
    ecdsaVerify.update(message.toArray)
    ecdsaVerify.verify(signature.toArray)
  }
}

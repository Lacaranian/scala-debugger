package org.scaladebugger.api.profiles.traits.info

import com.sun.jdi.Type

import scala.util.Try

/**
 * Represents the interface for retrieving type-based information.
 */
trait TypeInfoProfile extends CommonInfoProfile {
  /**
   * Returns the JDI representation this profile instance wraps.
   *
   * @return The JDI instance
   */
  override def toJdiInstance: Type

  /**
   * Represents the readable name for this type.
   *
   * @return The text representation of the type
   */
  def name: String

  /**
   * Represents the JNI-style signature for this type. Primitives have the
   * signature of their corresponding class representation such as "I" for
   * Integer.TYPE.
   *
   * @return The JNI-style signature
   */
  def signature: String

  /**
   * Returns whether or not this type represents an array type.
   *
   * @return True if an array type, otherwise false
   */
  def isArrayType: Boolean

  /**
   * Returns whether or not this type represents a class type.
   *
   * @return True if a class type, otherwise false
   */
  def isClassType: Boolean

  /**
   * Returns whether or not this type represents an interface type.
   *
   * @return True if an interface type, otherwise false
   */
  def isInterfaceType: Boolean

  /**
   * Returns whether or not this type represents a reference type.
   *
   * @return True if a reference type, otherwise false
   */
  def isReferenceType: Boolean

  /**
   * Returns whether or not this type represents a primitive type.
   *
   * @return True if a primitive type, otherwise false
   */
  def isPrimitiveType: Boolean

  /**
   * Returns whether or not this type is for a value that is null.
   *
   * @return True if representing the type of a null value, otherwise false
   */
  def isNullType: Boolean

  /**
   * Returns the type as an array type (profile).
   *
   * @return The array type profile wrapping this type
   */
  def toArrayType: ArrayTypeInfoProfile

  /**
   * Returns the type as an array type (profile).
   *
   * @return Success containing the array type profile wrapping this type,
   *         otherwise a failure
   */
  def tryToArrayType: Try[ArrayTypeInfoProfile] = Try(toArrayType)

  /**
   * Returns the type as an class type (profile).
   *
   * @return The class type profile wrapping this type
   */
  def toClassType: ClassTypeInfoProfile

  /**
   * Returns the type as an class type (profile).
   *
   * @return Success containing the class type profile wrapping this type,
   *         otherwise a failure
   */
  def tryToClassType: Try[ClassTypeInfoProfile] = Try(toClassType)

  /**
   * Returns the type as an interface type (profile).
   *
   * @return The interface type profile wrapping this type
   */
  def toInterfaceType: InterfaceTypeInfoProfile

  /**
   * Returns the type as an interface type (profile).
   *
   * @return Success containing the interface type profile wrapping this type,
   *         otherwise a failure
   */
  def tryToInterfaceType: Try[InterfaceTypeInfoProfile] = Try(toInterfaceType)

  /**
   * Returns the type as an reference type (profile).
   *
   * @return The reference type profile wrapping this type
   */
  def toReferenceType: ReferenceTypeInfoProfile

  /**
   * Returns the type as an reference type (profile).
   *
   * @return Success containing the reference type profile wrapping this type,
   *         otherwise a failure
   */
  def tryToReferenceType: Try[ReferenceTypeInfoProfile] = Try(toReferenceType)

  /**
   * Returns the type as an primitive type (profile).
   *
   * @return The primitive type profile wrapping this type
   */
  def toPrimitiveType: PrimitiveTypeInfoProfile

  /**
   * Returns the type as an primitive type (profile).
   *
   * @return Success containing the primitive type profile wrapping this type,
   *         otherwise a failure
   */
  def tryToPrimitiveType: Try[PrimitiveTypeInfoProfile] = Try(toPrimitiveType)

  /**
   * Returns a string presenting a better human-readable description of
   * the JDI instance.
   *
   * @return The human-readable description
   */
  override def toPrettyString: String = s"Type $name ($signature)"
}
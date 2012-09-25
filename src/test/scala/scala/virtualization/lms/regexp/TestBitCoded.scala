package scala.virtualization.lms.regexp

import org.scalatest._

class TestBitCoded extends Suite {
  import BitCoded._
  import Parsing._

  // ((ab)(c|d)|(abc))*
  val exE = Estar(Eplus(
    Eprod(Eprod(Echar('a'), Echar('b')), Eplus(Echar('c'), Echar('d'))),
    Eprod(Echar('a'), Eprod(Echar('b'), Echar('c')))))

  // p1 = [inl ((a; b); inr d); inr (a;(b; c))]
  val ex1 = Vstar(List(
    Vinl(Vprod(Vprod(Vchar('a'), Vchar('b')), Vinr(Echar('c'), Vchar('d'))),
         Eprod(Echar('a'), Eprod(Echar('b'), Echar('c')))),
    Vinr(Eprod(Eprod(Echar('a'), Echar('b')), Eplus(Echar('c'), Echar('d'))),
         Vprod(Vchar('a'), Vprod(Vchar('b'), Vchar('c'))))))(
    Eplus(
      Eprod(Eprod(Echar('a'), Echar('b')), Eplus(Echar('c'), Echar('d'))),
      Eprod(Echar('a'), Eprod(Echar('b'), Echar('c')))))
  // p2 = [inl ((a; b); inr d); inl ((a; b); inl c)] a
  val ex2 = Vstar(List(
    Vinl(Vprod(Vprod(Vchar('a'), Vchar('b')), Vinr(Echar('c'), Vchar('d'))),
         Eprod(Echar('a'), Eprod(Echar('b'), Echar('c')))),
    Vinl(Vprod(Vprod(Vchar('a'), Vchar('b')), Vinl(Vchar('c'), Echar('d'))),
         Eprod(Echar('a'), Eprod(Echar('b'), Echar('c'))))))(
    Eplus(
      Eprod(Eprod(Echar('a'), Echar('b')), Eplus(Echar('c'), Echar('d'))),
      Eprod(Echar('a'), Eprod(Echar('b'), Echar('c')))))

  val ex1code = "001011"
  val ex2code = "0010001"

  val exE2 = Eprod(Echar('a'), Eprod(Estar(Eplus(Echar('b'), Echar('c'))), Echar('a')))

  def testCode1 = {
    expect(ex1code){str(code(ex1))}
  }

  def testDecode1 = {
    expect(ex1){decode(exE)(unstr(ex1code))}
  }

  def testCode2 = {
    expect(ex2code){str(code(ex2))}
  }

  def testDecode2 = {
    expect(ex2){decode(exE)(unstr(ex2code))}
  }

  def testNfa = {
    val nfa = NFA.fromE(exE)
    val codes = NFA.run(nfa)("abdabc".toList)
    expect(Set(ex1code, ex2code))(codes.map(str))
  }

  def testNfa2 = {
    val nfa = NFA.fromE(exE2)
    val codes = NFA.run(nfa)("abca".toList)
    expect(Set("00011"))(codes.map(str))
  }
}

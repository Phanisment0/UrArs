package io.phanisment.urars.parser.math.ast;

public sealed interface Node permits CallNode, IdentifierNode, MemberNode, StringNode, NumberNode, BinaryNode, UnaryNode {}

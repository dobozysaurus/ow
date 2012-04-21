package net.vtst.ow.eclipse.js.closure.editor;

import com.google.javascript.rhino.JSDocInfo.Visibility;

import net.vtst.ow.eclipse.js.closure.OwJsClosureImages;

/**
 * The different kind of JavaScript elements.  Kinds are used to choose the right icon,
 * and to adapt some aspect of the behavior of the completion proposal.
 * @author Vincent Simonet
 */
public enum JSElementKind {
  NAMESPACE,
  CLASS,
  INTERFACE,
  ENUM,
  METHOD,
  FIELD,
  GLOBAL_VARIABLE,
  LOCAL_VARIABLE,
  CONSTANT,
  UNKNOWN;
}

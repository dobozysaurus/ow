package net.vtst.ow.eclipse.js.closure.launching;

import net.vtst.eclipse.easy.ui.properties.Record;
import net.vtst.eclipse.easy.ui.properties.fields.BooleanField;
import net.vtst.eclipse.easy.ui.properties.fields.StringOptionsField;

public class ClosureCompilerLaunchConfigurationRecord extends Record {
  
  public ClosureCompilerLaunchConfigurationRecord() {
    super.initializeByReflection();
  }
  
  public StringOptionsField compilationLevel = new StringOptionsField("WHITESPACE_ONLY", "SIMPLE_OPTIMIZATIONS", "ADVANCED_OPTIMIZATIONS");
  public BooleanField formattingPrettyPrint = new BooleanField(false);
  public BooleanField formattingPrintInputDelimiter = new BooleanField(false);
  public BooleanField generateExports = new BooleanField(false);

  private static ClosureCompilerLaunchConfigurationRecord instance;
  public static ClosureCompilerLaunchConfigurationRecord getInstance() {
    if (instance == null) instance = new ClosureCompilerLaunchConfigurationRecord();
    return instance;
  }

}

package net.vtst.ow.eclipse.js.closure.builder;

import java.io.File;

import net.vtst.eclipse.easy.ui.properties.stores.IStore;
import net.vtst.eclipse.easy.ui.properties.stores.PluginPreferenceStore;
import net.vtst.ow.closure.compiler.deps.JSExtern;
import net.vtst.ow.closure.compiler.deps.JSLibrary;
import net.vtst.ow.eclipse.js.closure.OwJsClosurePlugin;
import net.vtst.ow.eclipse.js.closure.compiler.IJSIncludesProvider;
import net.vtst.ow.eclipse.js.closure.preferences.ClosurePreferenceRecord;
import net.vtst.ow.eclipse.js.closure.util.WeakConcurrentHashMap;

import org.eclipse.core.runtime.CoreException;

import com.google.javascript.jscomp.AbstractCompiler;

/**
 * Registry of JavaScript libraries, which cache created libraries.  The cached libraries
 * are kept with weak references, so that they can be garbage collected if no more project uses
 * them.
 * <br>
 * <b>Thread safety:</b> This class is thread safe, as it uses a concurrent hash map.
 * @author Vincent Simonet
 */
public class JSIncludesProviderForBuilder implements IJSIncludesProvider {
  
  // **************************************************************************
  // Cache of libraries
  
  /**
   * The key identifying a library in the cache.
   */
  private static class LibraryKey {
    private File libraryPath;
    private File pathOfClosureBase;
    LibraryKey(File libraryPath, File pathOfClosureBase) {
      this.libraryPath = libraryPath;
      this.pathOfClosureBase = pathOfClosureBase;
    }
    public boolean equals(Object obj) {
      if (obj instanceof LibraryKey) {
        LibraryKey key = (LibraryKey) obj;
        return (
            key.libraryPath.equals(libraryPath) && 
            key.pathOfClosureBase.equals(pathOfClosureBase));
      }
      return false;
    }
    public int hashCode() {
      int h1 = libraryPath.hashCode();
      int h2 = pathOfClosureBase.hashCode();
      return h1 * h2 + h1 + h2;
    }
  }
  
  private WeakConcurrentHashMap<LibraryKey, JSLibrary> libraries = 
      new WeakConcurrentHashMap<LibraryKey, JSLibrary>();
  
  private WeakConcurrentHashMap<File, JSExtern> externs =
      new WeakConcurrentHashMap<File, JSExtern>();
  
  // **************************************************************************
  // Implementation of IJSLibraryProvider
  
  /* (non-Javadoc)
   * @see net.vtst.ow.eclipse.js.closure.compiler.IJSIncludesProvider#getLibrary(com.google.javascript.jscomp.AbstractCompiler, java.io.File, java.io.File)
   */
  public JSLibrary getLibrary(AbstractCompiler compiler, File libraryPath, File pathOfClosureBase) {
    LibraryKey key = new LibraryKey(libraryPath, pathOfClosureBase);
    JSLibrary library = libraries.get(key);
    if (library == null) {
      library = new JSLibrary(libraryPath, pathOfClosureBase, getCacheSettings());
      library.setUnits(compiler);
      libraries.put(key, library);
    }
    return library;
  }

  /* (non-Javadoc)
   * @see net.vtst.ow.eclipse.js.closure.compiler.IJSIncludesProvider#getExtern(com.google.javascript.jscomp.AbstractCompiler, java.io.File)
   */
  public JSExtern getExtern(AbstractCompiler compiler, File path) {
    JSExtern extern = externs.get(path);
    if (extern == null) {
      extern = new JSExtern(path);
      externs.put(path, extern);
    }
    return extern;
  }
    
  /* (non-Javadoc)
   * @see net.vtst.ow.eclipse.js.closure.compiler.IJSIncludesProvider#clear()
   */
  public void clear() {
    libraries.clear();
    externs.clear();
    cacheSettings = null;
  }
  
  // **************************************************************************
  // Preferences
  
  private JSLibrary.CacheSettings cacheSettings;
  
  private JSLibrary.CacheSettings getCacheSettings() {
    if (cacheSettings == null) {
      cacheSettings = getCacheSettingsFromPreferences();
    }
    return cacheSettings;
  }
  
  private JSLibrary.CacheSettings getCacheSettingsFromPreferences() {
    ClosurePreferenceRecord record = ClosurePreferenceRecord.getInstance();
    JSLibrary.CacheSettings result = new JSLibrary.CacheSettings();
    IStore store = new PluginPreferenceStore(OwJsClosurePlugin.getDefault().getPreferenceStore());
    try {
      result.cacheDepsFiles = record.cacheLibraryDepsFiles.get(store);
    } catch (CoreException e) {
      result.cacheDepsFiles = record.cacheLibraryDepsFiles.getDefault();
    }
    try {
      result.cacheStrippedFiles = record.cacheLibraryStrippedFiles.get(store);
    } catch (CoreException e) {
      result.cacheStrippedFiles = record.cacheLibraryStrippedFiles.getDefault();
    }
    return result;
  }
}

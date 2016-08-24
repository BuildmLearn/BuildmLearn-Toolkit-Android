package org.buildmlearn.toolkit.utilities;

import android.content.res.Resources;

import org.buildmlearn.toolkit.R;

import kellinwood.security.zipsigner.ResourceAdapter;

/**
 * @brief Provides internationalized progress and error strings to the zipsigner-lib API.
 */
class ZipSignerAppResourceAdapter implements ResourceAdapter {

    private final Resources resources;

    public ZipSignerAppResourceAdapter(Resources resources) {
        this.resources = resources;
    }

    @Override
    public String getString(Item item, Object... args) {
        switch (item) {
            case INPUT_SAME_AS_OUTPUT_ERROR:
                // return "Input and output files are the same.  Specify a different name for the output.";
                return resources.getString(R.string.InputFileSameAsOutput);
            case AUTO_KEY_SELECTION_ERROR:
                // return "Unable to auto-select key for signing " + args[0];
                return resources.getString(R.string.AutoKeySelectionError, args[0]);
            case LOADING_CERTIFICATE_AND_KEY:
                // return "Loading certificate and private key";
                return resources.getString(R.string.LoadingCertificateAnKey);
            case PARSING_CENTRAL_DIRECTORY:
                // return "Parsing the input's central directory";
                return resources.getString(R.string.ParsingCentralDirectory);
            case GENERATING_MANIFEST:
                // return "Generating manifest";
                return resources.getString(R.string.GeneratingManifest);
            case GENERATING_SIGNATURE_FILE:
                // return "Generating signature file";
                return resources.getString(R.string.GeneratingSignatureFile);
            case GENERATING_SIGNATURE_BLOCK:
                // return "Generating signature block file";
                return resources.getString(R.string.GeneratingSignatureBlock);
            case COPYING_ZIP_ENTRY:
                // return String.format("Copying zip entry %d of %d", args[0], args[1]);
                return resources.getString(R.string.CopyingZipEntry, args[0], args[1]);
            default:
                throw new IllegalArgumentException("Unknown item " + item);
        }
    }
}

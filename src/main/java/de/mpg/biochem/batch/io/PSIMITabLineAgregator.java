package de.mpg.biochem.batch.io;

import org.springframework.batch.item.file.transform.LineAggregator;

import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.builder.MitabWriterUtils;
import psidev.psi.mi.tab.model.builder.PsimiTabVersion;

public class PSIMITabLineAgregator implements LineAggregator<BinaryInteraction> {

	@Override
	public String aggregate(BinaryInteraction item) {
		return MitabWriterUtils.buildLine(item, PsimiTabVersion.v2_5).replace("\n", "");
	}
}

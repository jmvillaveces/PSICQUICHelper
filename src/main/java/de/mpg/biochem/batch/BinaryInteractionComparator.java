package de.mpg.biochem.batch;

import java.util.Comparator;

import psidev.psi.mi.tab.model.BinaryInteraction;

public class BinaryInteractionComparator implements Comparator<BinaryInteraction> {

	@Override
	public int compare(BinaryInteraction interactionA, BinaryInteraction interactionB) {
		String idA = getInteractionIdPair(interactionA), idB = getInteractionIdPair(interactionB);
		return idA.compareTo(idB);
	}
	
	private static String getInteractionIdPair(BinaryInteraction interaction){
		
		String intAId="";
		if(interaction.getInteractorA() != null){
			intAId = interaction.getInteractorA().getIdentifiers().get(0).getIdentifier();
		}
		
		String intBId="";
		if(interaction.getInteractorB() != null){
			intBId = interaction.getInteractorB().getIdentifiers().get(0).getIdentifier();
		}
		
		if(intAId.equals("")){
			intAId = intBId;
		}else if(intBId.equals("")){
			intBId = intAId;
		}
		
		int c = intAId.compareToIgnoreCase(intBId);
		if(c > 0)
			return intBId+"_"+intAId;
			
		return intAId+"_"+intBId;
	}
}

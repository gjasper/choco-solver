/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package solver.constraints.propagators.gary.tsp.undirected.relaxationHeldKarp;

import solver.constraints.propagators.gary.tsp.HeldKarp;
import solver.exception.ContradictionException;
import solver.variables.graph.INeighbors;

public class PrimOneTreeFinder extends PrimMSTFinder {

	//***********************************************************************************
	// CONSTRUCTORS
	//***********************************************************************************

	public PrimOneTreeFinder(int nbNodes, HeldKarp propagator) {
		super(nbNodes,propagator);
	}

	//***********************************************************************************
	// METHODS
	//***********************************************************************************

	@Override
	protected void prim() throws ContradictionException {
		minVal = propHK.getMinArcVal();
		if(FILTER){
			maxTArc = minVal;
		}
		inTree.set(0);
		INeighbors nei = g.getSuccessorsOf(0);
		int min1 = -1;
		int min2 = -1;
		boolean b1=false,b2=false;
		for(int j=nei.getFirstElement();j>=0;j=nei.getNextElement()){
			if(!b1){
				if(min1==-1){
					min1 = j;
				}
				if(costs[0][j]<costs[0][min1]){
					min2 = min1;
					min1 = j;
				}
				if(propHK.isMandatory(0,j)){
					if(min1!=j){
						min2 = min1;
					}
					min1 = j;
					b1 = true;
				}
			}
			if(min1!=j && !b2){
				if(min2==-1 || costs[0][j]<costs[0][min2]){
					min2 = j;
				}
				if(propHK.isMandatory(0,j)){
					min2 = j;
					b2 = true;
				}
			}
		}
		if(FILTER){
			if(!propHK.isMandatory(0,min1)){
				maxTArc = Math.max(maxTArc, costs[0][min1]);
			}
			if(!propHK.isMandatory(0,min2)){
				maxTArc = Math.max(maxTArc, costs[0][min2]);
			}
		}
		if(min1 == -1 || min2 == -1){
			propHK.contradiction();
		}
		int first=-1,sizeFirst=n+1;
		for(int i=1;i<n;i++){
			if(g.getSuccessorsOf(i).neighborhoodSize()<sizeFirst){
				first = i;
				sizeFirst = g.getSuccessorsOf(i).neighborhoodSize();
			}
		}
		if(first==-1){
			propHK.contradiction();
		}
		addNode(first);
		int from,to;
		while (tSize<n-2 && !heap.isEmpty()){
			to = heap.pop();
			from = heap.getMate(to);
			addArc(from,to);
		}
		if(tSize!=n-2){
			propHK.contradiction();
		}
		addArc(0,min1);
		addArc(0,min2);
		if(Tree.getNeighborsOf(0).neighborhoodSize()!=2){
			throw new UnsupportedOperationException();
		}
	}
}

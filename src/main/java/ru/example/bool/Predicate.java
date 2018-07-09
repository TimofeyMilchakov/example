/*********************************************************************
 * The Initial Developer of the content of this file is NOVARDIS.
 * All portions of the code written by NOVARDIS are property of
 * NOVARDIS. All Rights Reserved.
 *
 * NOVARDIS
 * Detskaya st. 5A, 199106 
 * Saint Petersburg, Russian Federation 
 * Tel: +7 (812) 331 01 71
 * Fax: +7 (812) 331 01 70
 * www.novardis.com
 *
 * (c) 2018 by NOVARDIS
 *********************************************************************/

package ru.example.bool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 09.07.2018
 */
@Setter
@Getter
public class Predicate
{
	private boolean not;

	private List<Predicate> variable;

	private final Operation operation;

	private final String nameVar;

	public Predicate(List<Predicate> variable, Operation operation, boolean not)
	{
		this.nameVar = null;
		this.variable = variable;
		this.operation = operation;
		this.not = not;
	}

	Predicate(String nameVar, boolean not)
	{
		this.nameVar = nameVar;
		this.not = not;
		this.variable = null;
		this.operation = null;
	}

	public Predicate and(Predicate input)
	{
		final Predicate predicateThis = this.getCopy();
		final Predicate predicateInput = input.getCopy();

		if (predicateThis.checkThisSimple())
		{
			if (predicateInput.checkThisSimple())
			{
				return new Predicate(new LinkedList<Predicate>()
				{{
					add(predicateInput);
					add(predicateThis);
				}}, Operation.AND, false);
			}
			else
			{
				if (predicateInput.operation == Operation.AND)
				{

				}
			}
		}

	}

	private List<Predicate> addListSimple(final List<Predicate> predicateF, final List<Predicate> predicateS)
	{
		List<Predicate> res = new LinkedList<>();
		List<Predicate> temp = new LinkedList<Predicate>()
		{{
			addAll(predicateF);
			addAll(predicateS);
		}};
		Predicate[] t = temp.toArray(new Predicate[temp.size()]);
		for (int i = 0; i < t.length; i++)
		{
			for (int j = i + 1; j < t.length - 1; j++)
			{
				if (t[i].checkNot(t[j]))
					return null;
				if (!t[i].equals(t[j]))
					res.add(t[i]);
			}
		}
		return res;
	}

	private List<Predicate> addListSimple(final List<Predicate> predicateF, final Predicate predicateS)
	{
		Predicate[] pF = predicateF.toArray(new Predicate[predicateF.size()]);
		List<Predicate> res = new LinkedList<>();
		for (int i = 0; i < pF.length; i++)
		{
			if(pF[i].checkNot(predicateS))
				return null;
			if(!pF[i].equals(predicateS))
				res.add(pF[i]);
		}
		res.add(predicateS);
		return res;
	}

	private Predicate getCopy()
	{
		Predicate predicate;
		if (checkThisSimple())
		{
			predicate = new Predicate(nameVar, not);
		}
		else
		{
			int size = this.variable.size();
			List<Predicate> variable = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
			{
				variable.add(this.variable.get(i).getCopy());
			}
			predicate = new Predicate(variable, this.operation, not);
		}
		return predicate;
	}

	private boolean checkNot(Predicate p)
	{
		if (p.checkThisSimple() && this.checkThisSimple())
		{
			if (p.nameVar.equals(this.nameVar) &&
							p.not != this.not)
				return true;
			return false;
		}
		else
		{
			List<Predicate> thisP = this.variable;
			List<Predicate> pP = p.variable;
			if (thisP.size() != pP.size() &&
							this.operation != p.operation &&
							this.not == p.not)
				return false;
			int size = pP.size();
			for (int i = 0; i < size; i++)
			{
				if (!thisP.get(i).equals(pP.get(i)))
					return false;
			}
			return true;
		}
	}


	private boolean checkThisSimple()
	{
		if ((nameVar == null) && (variable == null) ||
						((nameVar != null) && (variable != null)))
			throw new RuntimeException();
		if (nameVar != null)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof Predicate))
			return false;
		Predicate p = (Predicate) obj;
		if (p.checkThisSimple() && this.checkThisSimple())
		{
			if (p.nameVar.equals(this.nameVar) &&
							p.not == this.not)
				return true;
			return false;
		}
		else
		{
			List<Predicate> thisP = this.variable;
			List<Predicate> pP = p.variable;
			if (thisP.size() != pP.size())
				return false;
			int size = pP.size();
			if (this.operation != p.operation)
				return false;
			for (int i = 0; i < size; i++)
			{
				if (!thisP.get(i).equals(pP.get(i)))
					return false;
			}
			return true;
		}

	}

	public enum Operation
	{
		AND,
		OR
	}
}

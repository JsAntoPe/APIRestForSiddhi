package com.siddhiApi.services;

import com.siddhiApi.dao.PatternDAO;
import com.siddhiApi.entity.Pattern;
import com.siddhiApi.entity.Stream;
import com.siddhiApi.exceptions.NotFoundException;
import com.siddhiApi.util.PatternCodeChecker;
import com.siddhiApi.util.PatternCodeGeneratorMediator;
import io.siddhi.core.exception.SiddhiAppCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatternServiceImpl implements PatternService {

    private Logger logger = LoggerFactory.getLogger(PatternServiceImpl.class);

    @Autowired
    private PatternDAO patternDAO;

    @Override
    public void runPattern(Pattern pattern) throws Exception {
        try{
            pattern.setPatternCode(PatternCodeGeneratorMediator.getFullApplicationCode(pattern));
        }catch(NullPointerException e){
            throw new NullPointerException("There is a missing parameter in pattern.");
        }
        PatternCodeChecker.outputStreamCheck(pattern);
        patternDAO.runPattern(pattern);
    }

    @Override
    public List<String> getPatternsRunning() {
        return patternDAO.getPatternsRunning();
    }


    @Override
    public Pattern[] getPatterns() {
        return patternDAO.getPatterns();
    }

    @Override
    public Pattern getPattern(String id) throws NotFoundException {
        return patternDAO.getPattern(id);
    }

    @Override
    public void updatePattern(String patternName, Pattern patternToUpdate) throws NotFoundException, Exception {
        Pattern patternToBeRestoredInCaseRollback = patternDAO.getPattern(patternName);
        try {
            this.removePattern(patternName);
            logger.info("Pattern removed");
            this.runPattern(patternToUpdate);
        } catch (NotFoundException e) {
            throw new NotFoundException("The patterns was not found.");
        } catch (SiddhiAppCreationException e) {
            patternDAO.removePattern(patternToUpdate.getPatternName());
            patternDAO.runPattern(patternToBeRestoredInCaseRollback);
            throw new SiddhiAppCreationException(e);
        } catch (Exception e) {
            patternDAO.runPattern(patternToBeRestoredInCaseRollback);
            throw new Exception(e);
        }
    }

    @Override
    public void removePattern(String patternName) throws NotFoundException {
        patternDAO.removePattern(patternName);
        PatternCodeChecker.removePropertyOrderedInstance(patternName);
    }
}

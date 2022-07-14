import React, { useEffect, useState, ChangeEventHandler, ChangeEvent } from 'react';

import { useTheme } from '@emotion/react';
import FlexContainer from '../common/FlexContainer/FlexContainer';

import { PollInterface, PollItemInterface } from '../../types/poll';
import styled from '@emotion/styled';

import { getPollItems } from '../../api/poll';
import TextField from '../common/TextField/TextField';
import Radio from '../common/Radio/Radio';
import Checkbox from '../common/Checkbox/Checkbox';
import Input from '../common/Input/Input';

type PollItemIds = Array<PollItemInterface['id']>;

interface Props {
  pollId: PollInterface['id'];
  selectedPollItems: PollItemIds;
  allowedPollCount: PollInterface['allowedPollCount'];
  handleSelectPollItem: (mode: string) => (e: ChangeEvent<HTMLInputElement>) => void;
  setDescription: any; // TODO: any
}

function PollProgressItemGroup({
  pollId,
  selectedPollItems,
  handleSelectPollItem,
  allowedPollCount,
  setDescription
}: Props) {
  const theme = useTheme();
  const [pollItems, setPollItems] = useState<Array<PollItemInterface>>([]);

  useEffect(() => {
    const fetchPollItems = async (pollId: PollInterface['id']) => {
      const res = await getPollItems(pollId);

      setPollItems(res);
    };

    try {
      if (pollId) {
        fetchPollItems(pollId);
      }
    } catch (err) {
      alert(err);
    }
  }, []);

  return (
    <FlexContainer flexDirection="column" gap="1.2rem">
      {pollItems.map((pollItem: PollItemInterface) => {
        return (
          <>
            <TextField
              colorScheme={theme.colors.PURPLE_100}
              width="74.4rem"
              height="3.6rem"
              variant="outlined"
              borderRadius="10px"
            >
              {/* TODO: 상수화 */}
              {allowedPollCount >= 2 ? (
                <Checkbox
                  id={String(pollItem.id)}
                  checked={selectedPollItems.includes(pollItem.id)}
                  onChange={handleSelectPollItem('multiple')}
                >
                  {pollItem.subject}
                </Checkbox>
              ) : (
                  <Radio
                    id={String(pollItem.id)}
                    name={pollItem.subject}
                    checked={selectedPollItems.includes(pollItem.id)}
                    onChange={handleSelectPollItem('single')}
                  >
                    {pollItem.subject}
                  </Radio>
              )}
            </TextField>

            {/* description  */}
            <StyledDescription isSelected={selectedPollItems.includes(pollItem.id)}>
              <TextField
                  colorScheme={theme.colors.PURPLE_100}
                  width="74.4rem"
                  height="5rem"
                  variant="outlined"
                  borderRadius="10px"
              >
                <Input color={theme.colors.BLACK_100} fontSize="12px" placeholder='선택한 이유는?' onChange={(e) => setDescription(e.target.value)}/>
              </TextField>
            </StyledDescription>
          </>
        );
      })}
    </FlexContainer>
  );
}

const StyledDescription = styled.div(({ isSelected }: any | undefined) => `
  display: ${isSelected? 'block' : 'none'};
`);

export default PollProgressItemGroup;

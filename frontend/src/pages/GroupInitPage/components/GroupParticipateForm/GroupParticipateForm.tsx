import { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';

import { participateGroup } from '../../../../api/group';
import useInput from '../../../../hooks/useInput';

import GroupParticipateInvitationCodeInput from '../GroupParticipateInvitationCodeInput/GroupParticipateInvitationCodeInput';
import GroupParticipateFormSubmitButton from '../GroupPariticipateFormSubmitButton/GroupParticipateFormSubmitButton';
import FlexContainer from '../../../../components/FlexContainer/FlexContainer';
import { StyledForm } from './GroupParticipateForm.styles';

function GroupParticipateForm() {
  const [invitationCode, handleInvitationCode] = useInput('');

  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const res = await participateGroup(invitationCode);
      const groupCode = res.headers.location.split('/groups/')[1];
      navigate(`/groups/${groupCode}`);
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <StyledForm onSubmit={handleSubmit}>
      <FlexContainer>
        <GroupParticipateInvitationCodeInput
          invitationCode={invitationCode}
          handleInvitationCode={handleInvitationCode}
        />
        <GroupParticipateFormSubmitButton />
      </FlexContainer>
    </StyledForm>
  );
}

export default GroupParticipateForm;